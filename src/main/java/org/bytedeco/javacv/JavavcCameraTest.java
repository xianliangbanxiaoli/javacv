package org.bytedeco.javacv;

import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect.*;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import javax.swing.*;


//先把图像数据转化成Mat对象,Mat对象就像是一个容器,对图像的处理就是对Mat的处理.
public class JavavcCameraTest  {


    public static void main(String[] args) throws Exception{
        OpenCVFrameGrabber grabber =new OpenCVFrameGrabber(0);//新建opencv抓取器，一般的电脑和移动端设备中的设想头默认序号
        grabber.start();//开始获取摄像头数据

        CanvasFrame canvas=new CanvasFrame("人脸检测");//新建一个预览窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //窗口是否关闭
        while(true){
           if (!canvas.isEnabled()){
               //窗口是否关闭
               grabber.stop();//停止抓取
               System.exit(0);//推出
           }
           Frame frame=grabber.grab();
            OpenCVFrameConverter.ToMat convertor = new OpenCVFrameConverter.ToMat();//用于类型转换
           Mat scr=convertor.convertToMat(frame);//将获取frame转化mat重新转化为frame
            detectFace(scr);//人脸检测
            frame=convertor.convert(scr);//将检测结果重新的Mat重新转化为frame
            canvas.showImage(frame);//获取摄像头图像并放到窗口显示，frame是一帧视频图像
            Thread.sleep(50);//50毫秒刷新一次图像
        }

    }
    public static Mat detectFace(Mat src){
        Mat grayscr=new Mat();
        CascadeClassifier cascade = new CascadeClassifier("G:\\opencv3.2\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface.xml");//初始化人脸检测器
        cvtColor(src,grayscr,COLOR_BGRA2GRAY);
        equalizeHist(grayscr,grayscr);
        RectVector faces=new RectVector();
        cascade.detectMultiScale(grayscr,faces);
        for (int i=0;i<faces.size();i++){
            Rect face_i=faces.get(i);
            rectangle(src,face_i,new Scalar(0,0,225,1));//在原图画出人脸的区域
        }
        return  src;
    }

}

