package org.bytedeco.javacv;

import org.bytedeco.javacpp.opencv_core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;

import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;

public class FaceDetection {


    public static void FaceDetection(String cascadeClassifierXml, Integer width, Integer height) throws Exception{
        OpenCVFrameGrabber grabber =new OpenCVFrameGrabber(0);//新建opencv抓取器，一般的电脑和移动端设备中的设想头默认序号
        if (width!=null&&width>1&&height!=null&&height>1){
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
        }
        grabber.start();//开始获取摄像头数据
        if (width==null||height==null){
            height=grabber.getImageHeight();
            width=grabber.getImageWidth();
        }
        CanvasFrame canvas=new CanvasFrame("人脸检测");
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setVisible(true);
        canvas.setFocusable(true);
        //窗口置顶
        if (canvas.isAlwaysOnTopSupported()){
            canvas.setAlwaysOnTop(true);
        }
        Frame frame=null;
        //读取opencv人脸检测器
        CascadeClassifier cascade=new CascadeClassifier(cascadeClassifierXml);
        for (;canvas.isVisible()&&(frame=grabber.grab())!=null;){
            Mat img=(Mat) frame.opaque;//从frame中直接获取Mat

            Mat grayImg=new Mat();//存放灰意图
            //摄像头色彩模式设置成IMageMode.Gray下并不需要在做灰度
            cvtColor(img,grayImg, Imgproc.COLOR_BGRA2GRAY);
            //如果要获取摄像头灰度图，可以直接对FrameGrabber进行设置grabber.setImageMode(ImageMode.GRAY;)
            equalizeHist(grayImg,grayImg);
            //检测到的人脸
           opencv_core.RectVector faces=new opencv_core.RectVector();
            cascade.detectMultiScale(grayImg,faces);

            //遍历人脸
            for (int i=0;i<faces.size();i++){
                opencv_core.Rect face_i=faces.get(i);
                //绘制人脸矩形区域，scalar色彩顺序：BGR(蓝绿红)
                Imgproc.rectangle(img,face_i,new Scalar(0,255,0,1));
                int pos_x=Math.max(face_i.tl().x()-10,0);
                int pos_y=Math.max(face_i.tl().y()-10,0);
                //在人脸矩形上方绘制提示文字
                Imgproc.putText(img,"people face",new Point(pos_x,pos_y),FONT_HERSHEY,1.0,new Scalar(0,0,255,2.0));


            }
            canvas.showImage(frame);//获取摄像头图像放到窗口上显示，frame是一帧视频图像
            Thread.sleep(40);


        }
         cascade.close();
        canvas.dispose();
        grabber.close();


    }
}
