图片处理相关类

1.功能描述,详情见ImageGenerator接口类
*获取图片尺寸getSize
*添加图片水印watermarkByImage
*添加文字水印watermarkByText
*缩放resize

2).ImageGenerator有两个实现类，一个使用Java内置功能实现，一个使用im4java实现，其中im4java实现需要安装第三方软件ImageMagick或GraphicsMagick,推荐使用GraphicsMagick(http://www.graphicsmagick.org/)

3).使用方法见测试类/test/com/saysth/common/image/Im4javaImageGeneratorTest.java
