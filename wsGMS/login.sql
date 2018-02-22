SELECT
						a.`index` AS nomor_android,
						a.nomor AS nomor,
						a.password AS `password`,
						a.nomorthsales AS nomor_sales,
						d.kode AS kode_sales,
						a.kode AS nama,
						a.tipeuser AS tipe,
						a.nomorrole AS role,
						a.hash AS `hash`,
						IFNULL(a.telp, '') AS telp,
						a.nomorcabang AS cabang,
						e.cabang AS namacabang,
						b.isowner AS isowner,
						b.issales AS issales,
						b.setting AS setting,
						b.settingtarget AS settingtarget,
						b.salesorder AS salesorder,
						b.stockmonitoring AS stockmonitoring,
						b.pricelist AS pricelist,
						b.addscheduletask AS addscheduletask,
						b.salestracking AS salestracking,
						b.hpp AS hpp,
						b.crossbranch AS crossbranch,
						b.creategroup AS creategroup
					FROM gms_new.tuser a
					JOIN gms_new.whrole_mobile b ON a.nomorrole = b.nomor
					LEFT JOIN gms_new.thsales d ON a.nomorthsales = d.nomor
					JOIN gms_new.tcabang e ON a.nomorcabang = e.nomor
					WHERE a.status = 1
					AND a.kode = 'garjito'
					AND BINARY a.password = '020384'
					UNION SELECT
                        a.nomor AS nomor_android,
                        '' AS nomor,
                        a.password AS `password`,
                        '' AS nomor_sales,
                        a.kode AS kode_sales,
                        a.nama,
                        1 AS tipe,
                        '' AS role,
                        a.hash AS hash,
                        IFNULL(a.telepon, '') AS telp,
                        '' AS cabang,
                        '' AS namacabang,
                        '' AS isowner,
                        '' AS issales,
                        '' AS setting,
                        '' AS settingtarget,
                        '' AS salesorder,
                        '' AS stockmonitoring,
                        '' AS pricelist,
                        '' AS addscheduletask,
                        '' AS salestracking,
                        '' AS hpp,
                        '' AS crossbranch,
                        '' AS creategroup
                    FROM
                        gms_new.tcustomer a
                    WHERE a.aktif = 1
                        AND a.kode = 'garjito'
                        AND BINARY a.password = '020384'
                        AND a.kode != ''
                        AND a.password != ''