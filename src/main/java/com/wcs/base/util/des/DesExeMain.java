package com.wcs.base.util.des;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Project: tms</p>
 * <p>Description: 加密解密Exe启动主程序</p>
 * <p>Copyright © 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class DesExeMain {

    
    private static DESEncrypt desTool = null;
    
    private static Log log = LogFactory.getLog(DesExeMain.class);
    /**
     * 
     * <p>Description: 加密，解密程序主方法</p>
     * <p>参数说明（.代表当前目录）：</p>
     * <p>第一个参数：源文件（或目录）</p>
     * <p>第二个参数：加密文件（或目录）</p>
     * <p>第三个参数：加密0，解密1</p>
     * @param args 
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        desTool = new DESEncrypt();

        if (!checkArgs(args)) { 
        	return; 
        }

        long startTime = System.currentTimeMillis();
        String sourceFile = args[0];
        String destFile = args[1];
        String isEncrypt = args[2];

        sourceFile = FileUtils.filterFolderPath(sourceFile);
        destFile = FileUtils.filterFolderPath(destFile);

        log.info("当前操作目录：" + System.getProperty("user.dir"));

        // 加密
        if ("0".equals(isEncrypt)) {
            // 都是目录
            if (FileUtils.isDirectory(sourceFile) && FileUtils.isDirectory(destFile)) {
                batchEncryptFiles(sourceFile, destFile);
            }
            // 源文件参数 是文件，则只加密这一个文件
            if (!FileUtils.isDirectory(sourceFile) && FileUtils.isDirectory(destFile)) {
                encryptSingleFile(sourceFile, destFile);
            }
        }

        // 解密
        if ("1".equals(isEncrypt)) {
            // 都是目录 
            if (FileUtils.isDirectory(sourceFile) && FileUtils.isDirectory(destFile)) {
                batchDecryptFiles(sourceFile, destFile);
            }
            // 解密文件参数 是文件，则只解密这一个文件
            if (!FileUtils.isDirectory(destFile) && FileUtils.isDirectory(sourceFile)) {
                decryptSingleFile(destFile, sourceFile);
            }
        }
        long endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;
        String isEncryptDesc = isEncrypt.equals("0") ? "加密" : "解密";
        log.info("----------------------------------------------------");
        log.info("   完成【" + isEncryptDesc + "】,耗时：" + useTime + "毫秒");
        log.info("----------------------------------------------------");

    }

    /**
     * 
     * <p>Description: 读取源文件夹下文件列表(只考虑txt文件)，进行文件内容的加密，然后把加密的文件生成到目标文件夹下</p>
     * @param sourceFolder
     * @param destFolder
     * @return
     * @throws Exception
     */
    public static boolean batchEncryptFiles(String sourceFolder, String destFolder) throws Exception {
        File[] files = FileUtils.getFilesByPath(sourceFolder);
        for (int i = 0; i < files.length; i++) {
            encryptSingleFile(files[i].getAbsolutePath(), destFolder);
        }
        return true;
    }

    /**
     * 
     * <p>Description:加密一个txt文件，并拷贝至目标文件夹下 </p>
     * @param filePath
     * @param destFolder
     * @return
     * @throws Exception
     */
    public static boolean encryptSingleFile(String filePath, String destFolder) throws Exception {
        File file = new File(filePath);
        if (file.isFile() && file.getName().lastIndexOf(".txt") != -1) {
            String txtContext = FileUtils.getFileString(file);
            
            String encryptStr = desTool.encrypt(txtContext);
            // 生成的加密文件的文件名,原有的txt文件名后面追加.src
            String encryptFileName = file.getName() + ".src";
            
            FileUtils.write2FolderFromInput(destFolder, encryptFileName, FileUtils.stringToInputStream(encryptStr));
        }
        return true;
    }

    /**
     * 
     * <p>Description: 读取源文件夹下文件列表(只考虑src文件)，进行文件内容的解密，然后把解密的文件生成到目标文件夹下</p>
     * @param sourceFolder
     * @param destFolder
     * @return
     * @throws Exception
     */
    public static boolean batchDecryptFiles(String sourceFolder, String destFolder) throws Exception {
        File[] files = FileUtils.getFilesByPath(destFolder);
        for (int i = 0; i < files.length; i++) {
            decryptSingleFile(files[i].getAbsolutePath(), sourceFolder);
        }
        return true;
    }

    /**
     * 
     * <p>Description:加密一个txt文件，并拷贝至源文件夹下 </p>
     * @param filePath
     * @param destFolder
     * @return
     * @throws Exception
     */
    public static boolean decryptSingleFile(String filePath, String sourceFolder) throws Exception {
        File file = new File(filePath);
        if (file.isFile() && file.getName().lastIndexOf(".src") != -1) {
            String srcContext = FileUtils.getFileString(file);
            String sourceStr = desTool.decrypt(srcContext);
            // 生成的解密文件的文件名,去掉.src后缀
            String txtFileName = file.getName();
            String sourceFileName = txtFileName.substring(0, txtFileName.lastIndexOf(".src"));
            FileUtils.write2FolderFromInput(sourceFolder, sourceFileName, FileUtils.stringToInputStream(sourceStr));
        }
        return true;
    }

    /**
     * 
     * <p>Description:检查输入参数，是否有误 </p>
     * @param args
     * @return
     */
    public static boolean checkArgs(String[] args) {
        log.info("args.length:" + args.length);
        if (args.length != 3) {
            log.info("请输入三个参数，用空格隔开！");
            log.info("----------------------------");
            log.info("|1、源文件（或目录）路径;");
            log.info("|2、加密文件（或目录）路径;");
            log.info("|3、加密【0】，解密【1】。");
            log.info("----------------------------");
            return false;
        }
        String sourceFile = args[0];
        String destFile = args[1];
        String isEncrypt = args[2];

        if (!isEncrypt.equals("0") && !isEncrypt.equals("1")) {
            log.info("----------------------------------------------------");
            log.info("第三个参数输入有误：请输入0【加密】或1【解密】!");
            log.info("----------------------------------------------------");
            return false;
        }

        String isEncryptDesc = isEncrypt.equals("0") ? "加密" : "解密";
        log.info("----------------------------------------------------");
        log.info("您输入的txt源文件（或目录）是：" + sourceFile);
        log.info("您输入的src加密文件（或目录）是：" + destFile);
        log.info("您是想进行-->" + isEncryptDesc);
        log.info("----------------------------------------------------");

        return true;
    }

}
