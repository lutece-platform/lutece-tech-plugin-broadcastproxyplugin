--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'BROADCASTPROXY_MANAGEMENT_NEWSLETTERS';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('BROADCASTPROXY_MANAGEMENT_NEWSLETTERS','broadcastproxy.adminFeature.ManageSubscription.name',1,'jsp/admin/plugins/broadcastproxy/ManageSubscriptionLinks.jsp','broadcastproxy.adminFeature.ManageSubscription.description',0,'broadcastproxy',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'BROADCASTPROXY_MANAGEMENT_NEWSLETTERS';
INSERT INTO core_user_right (id_right,id_user) VALUES ('BROADCASTPROXY_MANAGEMENT_NEWSLETTERS',1);

--
-- Structure for table broadcastproxy_subscription_link
--

DROP TABLE IF EXISTS broadcastproxy_subscription_link;
CREATE TABLE broadcastproxy_subscription_link (
id_subscription_link int AUTO_INCREMENT,
label varchar(255) default '' NOT NULL,
pictogramme long varchar NOT NULL,
description long varchar NOT NULL,
frequency varchar(255) default '' NOT NULL,
subscription_group varchar(255) default '' NOT NULL,
group_id int default '0' NOT NULL,
subscription_id int default '0' NOT NULL,
interest_id int default '0' NOT NULL,
enabled smallint default '0',
PRIMARY KEY (id_subscription_link)
);