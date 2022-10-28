//package com.xiaomi.youpin.gwdash.service;
//
//import org.junit.Test;
//
//
//public class IAMServiceTest{
//
//    private IAMService iamService = new IAMService();
////    private IAMTest iamService = new IAMTest();
//
//    @Test
//    public void testDescribeTreeNode() {
//        String treeId = "14230";
//        String treeName = "arch";
//        String fullTreeName = iamService.getFullTreePath(treeName);
//
//        System.out.println("treeId : "+iamService.describeTreeNode(treeId));
//
//        System.out.println("treeName : "+iamService.describeTreeNode(fullTreeName));
//    }
//
//    @Test
//    public void saveTreeNode(){
//        String treeName = "test1-a1-a2";
////        String treeName = "test1_a1_a2";//下划线不允许
//        System.out.println("treeId : "+iamService.queryOrSaveTreeNode(treeName,"1112-a1-a2"));
//    }
//
//    @Test
//    public void deleteTreeNode(){
//        String treeId = "14245";
//        iamService.deleteTreeNode(treeId);
//    }
//}