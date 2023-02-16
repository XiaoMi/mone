# milog

# 代码规范
### 1、工程规范
#### 1.1 父工程pom职责边界
##### a) 通过modules管理子工程（重要）
##### b) 通过dependencyManagement全局管理包的版本（重要）
##### c) 通过dependency管理所有子工程会共同依赖的包

#### 1.2 各子module规范
##### a) 设计合理的分层，controller、job、mq三层为入口层，service为业务逻辑层，service的共同逻辑可以下沉为manager层
##### b) 严格规避出现循环依赖

### 2、代码规范
请参照《阿里巴巴开发手册》进行代码书写，安装p3c的idea插件和maven插件进行静态代码扫描

### 3、分支管理
##### a) intranet:上线分支，staging:测试环境分支

