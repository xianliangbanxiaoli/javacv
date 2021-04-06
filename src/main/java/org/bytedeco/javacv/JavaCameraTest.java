package org.bytedeco.javacv;

import javax.swing.JFrame;


public class JavaCameraTest {
    public static void main(String[] args) throws Exception, InterruptedException{
        OpenCVFrameGrabber grabber =new OpenCVFrameGrabber(0);//新建opencv抓取器，一般的电脑和移动端设备中的设想头默认序号
        grabber.start();//开始获取摄像头数据

        CanvasFrame canvas=new CanvasFrame("摄像头预览");//新建一个预览窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //窗口是否关闭
        while(canvas.isDisplayable()){
            //获取摄像头图像并在窗口显示，这里Frame frame=grabber.grab()得到是解码后的视频图像
            canvas.showImage(grabber.grab());
        }
        grabber.close();
    }
}
