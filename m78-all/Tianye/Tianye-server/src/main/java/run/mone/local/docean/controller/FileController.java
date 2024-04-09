//package run.mone.local.docean.controller;
//
//import com.xiaomi.youpin.docean.anno.Controller;
//import com.xiaomi.youpin.docean.anno.RequestMapping;
//import lombok.extern.slf4j.Slf4j;
//import run.mone.local.docean.po.Lock;
//
///**
// * @author wmin
// * @date 2024/2/22
// */
//@Slf4j
//@Controller
//public class FileController {
//
//
//    //接收文件的controller接口(class)
//    @RequestMapping(path = "/api/upload", method = RequestMethod.POST)
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
//        }
//        String fileName = file.getOriginalFilename();
//        // Assuming there is a method in LockMapper to save the file information
//        try {
//            // Here you would write the logic to save the file to the server
//            // For example, saving the file to a local directory or a cloud storage service
//            // After saving the file, you would save the file information using lockMapper
//            // Assuming the method to save file information is saveFile(Lock lock)
//            Lock lock = new Lock();
//            lock.setFileName(fileName);
//            lock.setFileSize(file.getSize());
//            lockMapper.saveFile(lock);
//            return ResponseEntity.ok("File uploaded successfully: " + fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload the file: " + fileName);
//        }
//    }
//
//}
