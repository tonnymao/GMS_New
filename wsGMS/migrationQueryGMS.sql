#NEW QUERY
#QUERY FOR ADDING MORE DATA TO TUSER GMS

ALTER TABLE gms.tuser
ADD column `telp` VARCHAR(15) after `password`,
ADD column `nomorthsales` INT NOT NULL after `pembuat`,
ADD column `nomorrole` INT NULL after `nomorthsales`,
ADD column `tipeuser` INT NOT NULL after `nomorrole`,
ADD column `token` VARCHAR(200) NOT NULL after `flagcabang`,
ADD column `hash` VARCHAR(200) NOT NULL after `status`;

UPDATE gms.tuser a, gms_web.whuser_mobile b
SET
    a.`nomorthsales` = b.`nomorthsales`,
    a.`nomorrole` = b.`nomorrole`,
    a.`token` = b.`token`,
    a.`tipeuser` = b.`tipeuser`,
    a.`hash` = b.`hash`
WHERE
	a.kode = b.userid
;
    
#END QUERY


#NEW QUERY
#ADD DATA PASSWORD TO TCUSTOMER GMS

ALTER TABLE gms.tcustomer
ADD column `userid` VARCHAR(100) NOT NULL after `kode`,
ADD column `password` VARCHAR(100) NOT NULL after `userid`,
ADD column `token` VARCHAR(200) NOT NULL after `pembuat`,
ADD column `hash` VARCHAR(200) NOT NULL after `token`;

UPDATE gms.tcustomer a, gms_new.tcustomer b
SET
	a.`userid` = b.`userid`,
	a.`password` = b.`password`,
    a.`token` = b.`token`,
    a.`hash` = b.`hash`
WHERE
	a.kode = b.kode
;

#END QUERY

#NEW QUERY
#MOVING TABLE WHROLE_MOBILE FROM GMS_WEB TO GMS

create table gms.whrole_mobile (
	nomor INT NOT NULL auto_increment PRIMARY KEY,
    nama VARCHAR(50) NOT NULL,
    isowner INT(1) DEFAULT 0,
    issales INT(1) DEFAULT 0,
    setting INT(1) DEFAULT 0,
    settingtarget INT(1) DEFAULT 0,
    salesorder INT(1) DEFAULT 0,
    stockmonitoring INT(1) DEFAULT 0,
    pricelist INT(1) DEFAULT 0,
    addscheduletask INT(1) DEFAULT 0,
    salestracking INT(1) DEFAULT 0,
    hpp INT(1) DEFAULT 0,
    crossbranch INT(1) DEFAULT 0,
    creategroup INT(1) DEFAULT 0,
    dibuat_pada TIMESTAMP
); 

insert into gms.whrole_mobile
select t.* 
FROM gms_web.whrole_mobile t;
    
#END QUERY

#NEW QUERY
#ADD TABLE whsetting_mobile INTO GMS

create table gms.whsetting_mobile (
	intNomor INT PRIMARY KEY NOT NULL auto_increment,
    vcNama VARCHAR(200) NOT NULL,
    intNilai VARCHAR(200) NOT NULL DEFAULT 0,
    dtInsertDate TIMESTAMP NOT NULL,
    intStatus INT NOT NULL DEFAULT 1
);

insert into gms.whsetting_mobile
select t.* 
FROM gms_web.whsetting_mobile t;

#END QUERY


#NEW QUERY
#ADD TABLE whtarget_mobile INTO GMS

CREATE TABLE gms.`whtarget_mobile` (
  `intNomor` int(11) NOT NULL AUTO_INCREMENT,
  `intNomorMSales` int(11) DEFAULT NULL,
  `vcKode` varchar(50) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `dtTanggal` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `intPeriode` int(11) DEFAULT NULL,
  `intTahun` int(11) DEFAULT NULL,
  `decTarget` decimal(30,6) NOT NULL DEFAULT '0.000000',
  `vcKeterangan` varchar(250) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `intApproved` tinyint(4) NOT NULL DEFAULT '0',
  `intApproveUserID` int(11) NOT NULL DEFAULT '0',
  `dtApproveTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `intDisapproveUserID` int(11) NOT NULL DEFAULT '0',
  `dtDisapproveTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `intOldApproved` tinyint(4) NOT NULL DEFAULT '0',
  `intNoApproval` tinyint(4) NOT NULL DEFAULT '0',
  `intInsertUserID` int(11) NOT NULL DEFAULT '0',
  `dtInsertTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `intUpdateUserID` int(11) NOT NULL DEFAULT '0',
  `dtUpdateTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `intDeleteUserID` int(11) NOT NULL DEFAULT '0',
  `dtDeleteTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `intStatus` tinyint(4) NOT NULL DEFAULT '1',
  `intPrinted` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`intNomor`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

insert into gms.whtarget_mobile
SELECT * FROM gms_web.whtarget_mobile;

#END QUERY


#NEW QUERY
#ADD TABLE whtracking_mobile INTO GMS

CREATE TABLE gms.`whtracking_mobile` (
  `nomor` int(11) NOT NULL AUTO_INCREMENT,
  `nomortuser` int(11) NOT NULL,
  `latitude` decimal(9,6) DEFAULT NULL,
  `longitude` decimal(9,6) DEFAULT NULL,
  `trackingDate` datetime NOT NULL,
  `fakeGPS` tinyint(4) NOT NULL,
  PRIMARY KEY (`nomor`)
) ENGINE=InnoDB AUTO_INCREMENT=818 DEFAULT CHARSET=latin1;

#END QUERY


#NEW QUERY
#ADD TABLE whgroup_mobile INTO GMS

CREATE TABLE gms.`whgroup_mobile` (
  `nomor` int(11) NOT NULL AUTO_INCREMENT,
  `nama` varchar(50) NOT NULL DEFAULT '0',
  `nomorwhuser` int(11) NOT NULL DEFAULT '0',
  `status_aktif` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nomor`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

insert into gms.whgroup_mobile
SELECT * FROM gms_web.whgroup_mobile;


#END QUERY


#NEW QUERY
#ADD TABLE whschedule_mobile INTO GMS

CREATE TABLE gms.`whschedule_mobile` (
  `nomor` int(11) NOT NULL AUTO_INCREMENT,
  `nomorwhuser_creator` int(11) NOT NULL DEFAULT '0',
  `nomorwhuser_tujuan` int(11) DEFAULT NULL,
  `nomortcustomer` int(11) DEFAULT NULL,
  `nomortcustomerprospecting` int(11) DEFAULT NULL,
  `nomorwhgroup` int(11) DEFAULT NULL,
  `tipejadwal` varchar(25) NOT NULL DEFAULT '0',
  `reminder` int(3) NOT NULL DEFAULT '0',
  `tanggal` date NOT NULL,
  `jam` time NOT NULL DEFAULT '00:00:00',
  `keterangan` varchar(250) NOT NULL,
  `status_selesai` int(1) NOT NULL DEFAULT '0',
  `status_aktif` int(1) NOT NULL DEFAULT '0',
  `tgl_buat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`nomor`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

insert into gms.whschedule_mobile
SELECT * FROM gms_web.whschedule_mobile;

#END QUERY



#NEW QUERY
#ADD TABLE whtdgroup_mobile INTO GMS

CREATE TABLE gms.`whtdgroup_mobile` (
  `nomor` int(11) NOT NULL AUTO_INCREMENT,
  `nomorwhgroup` int(11) NOT NULL DEFAULT '0',
  `nomorwhuser` int(11) NOT NULL DEFAULT '0',
  `nomorinsertby` int(11) NOT NULL DEFAULT '0',
  `nomorremoveby` int(11) NOT NULL DEFAULT '0',
  `tgl_buat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status_aktif` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`nomor`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;


insert into gms.whtdgroup_mobile
SELECT * FROM gms_web.whtdgroup_mobile;

#END QUERY