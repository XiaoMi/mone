+ 需要在tpc上开通账户(staing是 1 和 2),线上也需要开通
+ redis梳理
    + filter的存放地址是redis
    + api 的mock 数据存放地址是redis
+ 表结构梳理
+ 每个id都加一个特别大的数字比如10000000
+ filter_info 有重叠(存储filter信息的)
+ gw_group_info 有重叠(需要修改数据)
+ api_info
    + group_id 分组id --> api_group_info
    + filter_params -->filter_info 里边存着id 是个json  (注意有部分数据存储到json里了)
+ 需要写一份代码能够导出数据库的数据到新的数据源(mysql+redis)
+ filter cache 同步的解法(主要是key值修改,加入tenant)
    + 数据库中filter合并
    + 调用/api/filter/redis/flesh 刷新到cache中(filter审核,同步redis)
    + 网关中的redis统一成一个
    + 发布新版本的tesla网关