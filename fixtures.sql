# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.6.14)
# Datenbank: tuk
# Erstellungsdauer: 2014-02-04 16:35:56 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Export von Tabelle Customer
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Customer`;

CREATE TABLE `Customer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `street` varchar(255) DEFAULT NULL,
  `streetnumber` varchar(45) DEFAULT NULL,
  `postcode` varchar(45) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `customerid_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `Customer` WRITE;
/*!40000 ALTER TABLE `Customer` DISABLE KEYS */;

INSERT INTO `Customer` (`id`, `name`, `street`, `streetnumber`, `postcode`, `city`, `firstname`, `lastname`, `phone`, `email`)
VALUES
	(1,'Color Inc.','Fasanenweg','57','14482','Potsdam','Detlef','Dubronski','331765432','info@color-inc.de'),
	(2,'Example GmbH','Mühlenweg','1','14482','Potsdam','Franz','Schmidt','331234567','franz.schmidt@examplegmbh.de');

/*!40000 ALTER TABLE `Customer` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle IncomingGoods
# ------------------------------------------------------------

DROP TABLE IF EXISTS `IncomingGoods`;

CREATE TABLE `IncomingGoods` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `productid` int(10) unsigned NOT NULL,
  `amount` int(11) NOT NULL,
  `incomedate` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `incominggoodsid_UNIQUE` (`id`),
  KEY `fk_IncomingGoods_Product_idx` (`productid`),
  CONSTRAINT `fk_IncomingGoods_Product` FOREIGN KEY (`productid`) REFERENCES `Product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `IncomingGoods` WRITE;
/*!40000 ALTER TABLE `IncomingGoods` DISABLE KEYS */;

INSERT INTO `IncomingGoods` (`id`, `productid`, `amount`, `incomedate`)
VALUES
	(1,1,20,'2012-05-05 00:00:00');

/*!40000 ALTER TABLE `IncomingGoods` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle Invoice
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Invoice`;

CREATE TABLE `Invoice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `outboundid` int(10) unsigned NOT NULL,
  `billingdate` date NOT NULL,
  `paid` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `outboundid_UNIQUE` (`outboundid`),
  UNIQUE KEY `invoiceid_UNIQUE` (`id`),
  KEY `fk_Invoice_OutboundDelivery_idx` (`outboundid`),
  CONSTRAINT `fk_Invoice_OutboundDelivery` FOREIGN KEY (`outboundid`) REFERENCES `OutboundDelivery` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `Invoice` WRITE;
/*!40000 ALTER TABLE `Invoice` DISABLE KEYS */;

INSERT INTO `Invoice` (`id`, `outboundid`, `billingdate`, `paid`)
VALUES
	(1,4,'2012-04-05','2012-05-06'),
	(2,1,'2012-05-14','2012-05-16'),
	(3,2,'2012-04-14',NULL),
	(4,3,'2013-08-20',NULL),
	(5,10,'2013-11-06',NULL),
	(6,11,'2013-09-09',NULL),
	(7,13,'2013-09-11',NULL);

/*!40000 ALTER TABLE `Invoice` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle OutboundDelivery
# ------------------------------------------------------------

DROP TABLE IF EXISTS `OutboundDelivery`;

CREATE TABLE `OutboundDelivery` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sentdate` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `outboundid_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `OutboundDelivery` WRITE;
/*!40000 ALTER TABLE `OutboundDelivery` DISABLE KEYS */;

INSERT INTO `OutboundDelivery` (`id`, `sentdate`)
VALUES
	(1,'2012-04-14'),
	(2,'2012-04-14'),
	(3,'2012-04-10'),
	(4,'2012-05-04'),
	(10,'2013-11-06'),
	(11,'2013-11-09'),
	(12,'2013-11-11'),
	(13,'2013-11-11');

/*!40000 ALTER TABLE `OutboundDelivery` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle OverdueNotice
# ------------------------------------------------------------

DROP TABLE IF EXISTS `OverdueNotice`;

CREATE TABLE `OverdueNotice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `customerid` int(10) unsigned NOT NULL,
  `invoiceid` int(10) unsigned NOT NULL,
  `amount` double NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `overduenoticeid_UNIQUE` (`id`),
  KEY `fk_IssuedDunning_Customer_idx` (`customerid`),
  KEY `fk_IssuedDunning_Invoice_idx` (`invoiceid`),
  CONSTRAINT `fk_IssuedDunning_Customer` FOREIGN KEY (`customerid`) REFERENCES `Customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_IssuedDunning_Invoice` FOREIGN KEY (`invoiceid`) REFERENCES `Invoice` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `OverdueNotice` WRITE;
/*!40000 ALTER TABLE `OverdueNotice` DISABLE KEYS */;

INSERT INTO `OverdueNotice` (`id`, `customerid`, `invoiceid`, `amount`, `date`)
VALUES
	(2,2,4,184.50719999999995,'2013-11-09'),
	(3,1,6,1542.962,'2013-11-09'),
	(4,1,6,1851.5544,'2013-11-09'),
	(5,1,7,623.4743999999998,'2013-11-11');

/*!40000 ALTER TABLE `OverdueNotice` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle PaymentConstraint
# ------------------------------------------------------------

DROP TABLE IF EXISTS `PaymentConstraint`;

CREATE TABLE `PaymentConstraint` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `paymenttermid` int(10) unsigned NOT NULL,
  `daysoffset` int(11) NOT NULL,
  `discountfactor` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_PTConstraint_PaymentTerm1_idx` (`paymenttermid`),
  CONSTRAINT `fk_PTConstraint_PaymentTerm1` FOREIGN KEY (`paymenttermid`) REFERENCES `PaymentTerm` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `PaymentConstraint` WRITE;
/*!40000 ALTER TABLE `PaymentConstraint` DISABLE KEYS */;

INSERT INTO `PaymentConstraint` (`id`, `paymenttermid`, `daysoffset`, `discountfactor`)
VALUES
	(1,1,10,0.95),
	(2,1,20,0.97),
	(3,1,30,1),
	(4,1,40,1.2),
	(5,1,0,0.9);

/*!40000 ALTER TABLE `PaymentConstraint` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle PaymentReceipt
# ------------------------------------------------------------

DROP TABLE IF EXISTS `PaymentReceipt`;

CREATE TABLE `PaymentReceipt` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `invoiceid` int(10) unsigned NOT NULL,
  `value` double NOT NULL,
  `paymentdate` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `receiptid_UNIQUE` (`id`),
  KEY `fk_PaymentReceipt_Invoice_idx` (`invoiceid`),
  CONSTRAINT `fk_PaymentReceipt_Invoice` FOREIGN KEY (`invoiceid`) REFERENCES `Invoice` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `PaymentReceipt` WRITE;
/*!40000 ALTER TABLE `PaymentReceipt` DISABLE KEYS */;

INSERT INTO `PaymentReceipt` (`id`, `invoiceid`, `value`, `paymentdate`)
VALUES
	(1,1,233.5,'2012-05-08'),
	(18,4,5,'2013-11-08'),
	(20,4,6,'2013-11-08'),
	(41,4,40,'2013-11-09'),
	(46,7,0.5,'2013-11-14');

/*!40000 ALTER TABLE `PaymentReceipt` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle PaymentTerm
# ------------------------------------------------------------

DROP TABLE IF EXISTS `PaymentTerm`;

CREATE TABLE `PaymentTerm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `paymenttermid_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `PaymentTerm` WRITE;
/*!40000 ALTER TABLE `PaymentTerm` DISABLE KEYS */;

INSERT INTO `PaymentTerm` (`id`, `description`)
VALUES
	(1,'30 days net, 5% discount for 10 days, 3% disc');

/*!40000 ALTER TABLE `PaymentTerm` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle Product
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Product`;

CREATE TABLE `Product` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `unit` varchar(45) NOT NULL,
  `pprice` double NOT NULL,
  `defaultmargin` double NOT NULL DEFAULT '0.3',
  `stocklevel` int(11) NOT NULL,
  `shippingcost` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `Product` WRITE;
/*!40000 ALTER TABLE `Product` DISABLE KEYS */;

INSERT INTO `Product` (`id`, `name`, `unit`, `pprice`, `defaultmargin`, `stocklevel`, `shippingcost`)
VALUES
	(1,'Putzifix','Litre',1.4,0.09,3500,0.55),
	(2,'Moltoflex 2000','Unit',20.01,0.98,40000,20),
	(3,'Moltoflex 3000','Unit',23.98,1.01,20000,1.02);

/*!40000 ALTER TABLE `Product` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle SalesOrder
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SalesOrder`;

CREATE TABLE `SalesOrder` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `customerid` int(10) unsigned NOT NULL,
  `paymenttermid` int(10) unsigned NOT NULL,
  `priority` int(11) NOT NULL,
  `discountfactor` double NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `salesorderid_UNIQUE` (`id`),
  KEY `fk_SalesOrder_Customer_idx` (`customerid`),
  KEY `fk_SalesOrder_PaymentTerm_idx` (`paymenttermid`),
  CONSTRAINT `fk_SalesOrder_Customer` FOREIGN KEY (`customerid`) REFERENCES `Customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SalesOrder_PaymentTerm` FOREIGN KEY (`paymenttermid`) REFERENCES `PaymentTerm` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `SalesOrder` WRITE;
/*!40000 ALTER TABLE `SalesOrder` DISABLE KEYS */;

INSERT INTO `SalesOrder` (`id`, `customerid`, `paymenttermid`, `priority`, `discountfactor`, `status`)
VALUES
	(1,1,1,3,0.8009999999999999,'rejected'),
	(2,2,1,2,1,'sent'),
	(3,1,1,3,1,'invoiced'),
	(4,2,1,2,1,'invoiced'),
	(5,1,1,3,1,'released'),
	(6,1,1,3,0.9997,'submitted'),
	(7,1,1,3,0.9,'invoiced'),
	(8,1,1,2,1,'invoiced');

/*!40000 ALTER TABLE `SalesOrder` ENABLE KEYS */;
UNLOCK TABLES;


# Export von Tabelle SalesOrderLineItem
# ------------------------------------------------------------

DROP TABLE IF EXISTS `SalesOrderLineItem`;

CREATE TABLE `SalesOrderLineItem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `salesorderid` int(10) unsigned NOT NULL,
  `productid` int(10) unsigned NOT NULL,
  `outboundid` int(10) unsigned DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `price` double NOT NULL,
  `promisedate` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_SoPosition_SalesOrder_idx` (`salesorderid`),
  KEY `fk_SoPosition_Product_idx` (`productid`),
  KEY `fk_SoPosition_OutboundDelivery_idx` (`outboundid`),
  CONSTRAINT `fk_SoPosition_OutboundDelivery` FOREIGN KEY (`outboundid`) REFERENCES `OutboundDelivery` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SoPosition_Product` FOREIGN KEY (`productid`) REFERENCES `Product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_SoPosition_SalesOrder` FOREIGN KEY (`salesorderid`) REFERENCES `SalesOrder` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `SalesOrderLineItem` WRITE;
/*!40000 ALTER TABLE `SalesOrderLineItem` DISABLE KEYS */;

INSERT INTO `SalesOrderLineItem` (`id`, `salesorderid`, `productid`, `outboundid`, `quantity`, `price`, `promisedate`)
VALUES
	(1,1,1,NULL,18,26.82,'2012-05-15'),
	(2,1,3,NULL,16,399.84,'2012-05-15'),
	(3,2,2,12,30,20.99,'2012-05-10'),
	(4,2,3,12,20,24.99,'2012-05-10'),
	(5,3,1,10,15,1.49,'2012-04-14'),
	(6,3,3,10,20,24.99,'2012-04-14'),
	(7,4,1,3,50,1.49,'2014-04-20'),
	(8,4,2,3,10,20.99,'2014-04-20'),
	(9,5,1,4,10,1.49,'2012-04-21'),
	(10,5,2,4,10,20.99,'2012-04-21'),
	(13,1,2,NULL,5,104.95,'2013-10-17'),
	(14,6,2,NULL,4,83.96,'2013-10-16'),
	(16,7,1,11,50,74.5,'2013-11-09'),
	(17,7,2,11,10,209.9,'2013-11-09'),
	(22,8,2,13,4,83.96,'2013-11-06');

/*!40000 ALTER TABLE `SalesOrderLineItem` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
