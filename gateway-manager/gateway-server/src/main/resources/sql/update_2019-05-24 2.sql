alter table gateway_server_info add ctime bigint;
alter table gateway_server_info add utime bigint;
alter table plugin_info add group_info varchar(200);
alter table tesla_ds add description varchar(400);