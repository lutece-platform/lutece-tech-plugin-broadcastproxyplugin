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
