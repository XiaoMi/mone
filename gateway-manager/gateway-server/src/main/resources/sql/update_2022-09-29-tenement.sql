alter table gw_group_info add tenement varchar(30);

alter table api_info add tenement varchar(30);

alter table filter_info add tenement varchar(30);

alter table tesla_ds add tenement varchar(30);

alter table metadata add tenant varchar(30);

alter table plugin_info add tenant varchar(30);

alter table api_info drop index uk_url;

alter table api_info add unique index (url,tenement);

alter table filter_info drop index name;

alter table filter_info add unique index (name,tenement);

alter table gw_user_info add tenant varchar(30);
alter table plugin_data add tenant varchar(30);
alter table gateway_server_info add tenant varchar(30);

ALTER TABLE `gw_user_info` MODIFY COLUMN `gids` varchar(900)



