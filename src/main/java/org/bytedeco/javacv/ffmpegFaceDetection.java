package org.bytedeco.javacv;

import org.bytedeco.opencv.global.opencv_cudaimgproc;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_COMPLEX;

public class ffmpegFaceDetection {


    /**
     * 人脸检测
     * @param input 视频源
     * @param cascadeClassifierXml 基于Haar特征的cascade正面人脸分类器
     * @param width 图像宽度
     * @param height 图像高度
     */
    public static void ffmpegFaceDetection(String input,String cascadeClassifierXml,Integer width,Integer height) throws Exception, InterruptedException {
        // 读取视频文件或者视频流获取图像（得到的图像为frame类型，需要转换为mat类型进行检测和识别）
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
        if(width!=null&&width>1&&height!=null&&height>1) {
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
        }
        grabber.start();
        if(width==null||height==null) {
            height=grabber.getImageHeight();
            width=grabber.getImageWidth();
        }

        //Frame与Mat转换器
        OpenCVFrameConverter.ToMat converter=new OpenCVFrameConverter.ToMat();

        CanvasFrame canvas = new CanvasFrame("人脸检测");// 新建一个预览窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setVisible(true);
        canvas.setFocusable(true);
        //窗口置顶
        if(canvas.isAlwaysOnTopSupported()) {
            canvas.setAlwaysOnTop(true);
        }
        Frame frame =null;

        // 读取opencv人脸检测器，参考我的路径改为自己的路径
        CascadeClassifier cascade = new CascadeClassifier(cascadeClassifierXml);

        //ffmpegFrameGrabber读取图片与视频不同的是，图片只需要调用一次grabber.grabImage()即可，视频需要循环一直调用，直到没有视频帧为止。
        //只获取图像帧
        for(;canvas.isVisible()&&(frame=grabber.grabImage())!=null;) {

            //FFmpegFrameGrabber获取的opaque是AvFrame,所以需要转换
            Mat img =converter.convert(frame);// 将获取的frame转化成mat数据类型

            Mat grayImg = new Mat();//存放灰度图
            //摄像头色彩模式设置成ImageMode.Gray下不需要再做灰度
          opencv_imgproc.cvtColor(img, grayImg, COLOR_BGRA2GRAY);// 摄像头获取的是彩色图像，所以先灰度化下
            //如果要获取摄像头灰度图，可以直接对FrameGrabber进行设置grabber.setImageMode(ImageMode.GRAY);，grabber.grab()获取的都是灰度图

            opencv_cudaimgproc.equalizeHist(grayImg, grayImg);// 均衡化直方图

            // 存放检测到的人脸
            RectVector faces = new RectVector();
            //批量检测人脸
            cascade.detectMultiScale(grayImg, faces);

            // 遍历人脸
            for (int i = 0; i < faces.size(); i++) {
                Rect face_i = faces.get(i);
                //绘制人脸矩形区域，scalar色彩顺序：BGR(蓝绿红)
               opencv_imgproc.rectangle(img, face_i, new Scalar(0, 0, 255, 1));

                int pos_x = Math.max(face_i.tl().x() - 10, 0);
                int pos_y = Math.max(face_i.tl().y() - 10, 0);
                // 在人脸矩形上面绘制文字
                opencv_imgproc.putText(img, "people face", new Point(pos_x, pos_y), FONT_HERSHEY_COMPLEX, 1.0,new Scalar(0, 0, 255, 2.0));
            }

            canvas.showImage(frame);// 获取摄像头图像并放到窗口上显示，frame是一帧视频图像
        }
        cascade.close();
        canvas.dispose();
        grabber.close();// 停止抓取
    }
}
