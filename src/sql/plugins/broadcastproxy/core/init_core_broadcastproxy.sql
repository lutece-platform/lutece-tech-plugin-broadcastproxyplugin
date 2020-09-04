
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'BROADCASTPROXY_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('BROADCASTPROXY_MANAGEMENT','broadcastproxy.adminFeature.ManageBroadcastProxy.name',1,'jsp/admin/plugins/broadcastproxy/ManageBroadcastProxy.jsp','broadcastproxy.adminFeature.ManageBroadcastProxy.description',0,'broadcastproxy',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'BROADCASTPROXY_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('BROADCASTPROXY_MANAGEMENT',1);

--
-- add site property (as datastore key)
--
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('broadcastproxy.site_property.mydashboard.feedtypes', 'ALERT,NEWSLETTER');


