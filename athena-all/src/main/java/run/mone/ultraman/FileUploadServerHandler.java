package run.mone.ultraman;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = Logger.getLogger(FileUploadServerHandler.class.getName());
    //浏览器发来的request请求，转化为了这个对象
    private HttpRequest request;

    private boolean readingChunks;

    private final StringBuilder responseContent = new StringBuilder();

    private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed
    //发文件需要post请求，过来数据之后需要解码post
    private HttpPostRequestDecoder decoder;

    static {
        //这是存储上传文件和属性的位置的配置，如果不存在该文件夹会报错。
        DiskFileUpload.deleteOnExitTemporaryFile = true; //当文件出现重名的时候是否删除
        DiskFileUpload.baseDirectory = "D:" + File.separatorChar + "aa"; // 系统存储文件的位置
        DiskAttribute.deleteOnExitTemporaryFile = true; //如果属性出现重复选择删掉
        DiskAttribute.baseDirectory = "D:" + File.separatorChar + "aa"; // 属性文件存储目录。
    }

    /**
     * 当通道断开的时候，触发此事件，置空decoder
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    /**
     * 当收到数据的时候，触发的方法，类似channelRead
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            URI uri = new URI(request.uri());
            if (uri.getPath().startsWith("/audio")) {
                decoder = new HttpPostRequestDecoder(factory, request);
                readHttpDataChunkByLoacl();
                return;
            } else {
                ctx.fireChannelRead(msg);
            }
        } else if (msg instanceof HttpContent) {
            decoder.offer((HttpContent) msg);
            readHttpDataChunkByLoacl();
        }
    }

    private void readHttpDataChunkByLoacl() throws IOException {
        while (null != decoder && decoder.hasNext()) {
            InterfaceHttpData data = decoder.next();
            if (data != null) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        fileUpload.isInMemory();

                        // 获取文件
                        //                       File file = fileUpload.getFile();
//                        FileInputStream fileInputStream = new FileInputStream(file);

                        String filename = fileUpload.getFilename();
                        // 截取后缀名
                        String suffixName = "";
                        if (filename.contains(".")) {
                            suffixName = filename.substring(filename.lastIndexOf("."));
                        } else {
                            suffixName = filename;
                        }
//                        //TODO 重命名文件名称
//                        fileUpload.setFilename(new Random().nextInt(9999) + "" + suffixName);
//                        // 将重命名后的文件存放于 saveFilePath 路径下
//                        fileUpload.renameTo(new File(saveFilePath + File.separator + fileUpload.getFilename()));
//                        decoder.removeHttpDataFromClean(fileUpload);
                    }
                }
            }
        }
    }

    /**
     * 功能描述: 连接出现异常会调用此方法，一般就是记录日志，关闭连接。
     *
     * @auther: 李泽
     * @date: 2019/3/17 11:47
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.WARNING, responseContent.toString(), cause);
        ctx.channel().close();
    }
}

