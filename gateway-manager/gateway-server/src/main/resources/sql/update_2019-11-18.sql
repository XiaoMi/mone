ALTER TABLE `api_group_info`
	ADD COLUMN `gid` int(11) NOT NULL DEFAULT 0;

update `api_group_info` set gid=id;
