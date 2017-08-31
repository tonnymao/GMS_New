<?php

defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH . '/libraries/REST_Controller.php';

/**
 * This is an example of a few basic event interaction methods you could use
 * all done with a hardcoded array
 *
 * @package         CodeIgniter
 * @subpackage      Rest Server
 * @category        Controller
 * @author          Phil Sturgeon, Chris Kacerguis
 * @license         MIT
 * @link            https://github.com/chriskacerguis/codeigniter-restserver
 */
class Sales extends REST_Controller { 

    function __construct()
    {
        // Construct the parent class
        parent::__construct();

        // Configure limits on our controller methods
        // Ensure you have created the 'limits' table and enabled 'limits' within application/config/rest.php
        $this->methods['event_post']['limit'] = 500000000; // 500 requests per hour per event/key
        // $this->methods['event_delete']['limit'] = 50; // 50 requests per hour per event/key
        $this->methods['event_get']['limit'] = 500000000; // 500 requests per hour per event/key

        header("Access-Control-Allow-Origin: *");
        header("Access-Control-Allow-Methods: GET, POST");
        header("Access-Control-Allow-Headers: Origin, Content-Type, Accept, Authorization");
    }

	function ellipsis($string) {
        $cut = 30;
        $out = strlen($string) > $cut ? substr($string,0,$cut)."..." : $string;
        return $out;
    }
	
    function clean($string) {
        return preg_replace("/[^[:alnum:][:space:]]/u", '', $string); // Replaces multiple hyphens with single one.
    }

    function error($string) {
        return str_replace( array("\t", "\n") , "", $string);
    }

    public function send_gcm($registrationId,$message,$title,$fragment,$nomor,$nama)
    {
        $this->load->library('gcm');

        $this->gcm->setMessage($message);
        $this->gcm->setTitle($title);
        $this->gcm->setFragment($fragment);
        $this->gcm->setNomor($nomor);
        $this->gcm->setNama($nama);

        $this->gcm->addRecepient($registrationId);

        $this->gcm->setData(array(
            'some_key' => 'some_val'
        ));

        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);

        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);

        $this->gcm->send();

       if ($this->gcm->send())
           echo 'Success for all messages';
       else
           echo 'Some messages have errors';

       print_r($this->gcm->status);
       print_r($this->gcm->messagesStatuses);

        die(' Worked.');
    }

	public function send_gcm_group($registrationId,$message,$title,$fragment,$nomor,$nama)
    {
        $this->load->library('gcm');

        $this->gcm->setMessage($message);
        $this->gcm->setTitle($title);
        $this->gcm->setFragment($fragment);
        $this->gcm->setNomor($nomor);
        $this->gcm->setNama($nama);

        foreach ($registrationId as $regisID) {
            $this->gcm->addRecepient($regisID);
        }

        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);

        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);

        $this->gcm->send();
    }
	
	// --- POST insert new order jual --- //
	//added by Tonny
	function insertNewOrderJual_post()
	{     
        $data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));

		$kode = (isset($jsonObject["kode"]) ? $this->clean($jsonObject["kode"])     : "");
        $tanggal = (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $nomorcustomer = (isset($jsonObject["nomorcustomer"]) ? $this->clean($jsonObject["nomorcustomer"])     : "");
        $kodecustomer = (isset($jsonObject["kodecustomer"]) ? $this->clean($jsonObject["kodecustomer"])     : "");
        $nomorbroker = (isset($jsonObject["nomorbroker"]) ? $this->clean($jsonObject["nomorbroker"])     : "");
        $kodebroker = (isset($jsonObject["kodebroker"]) ? $this->clean($jsonObject["kodebroker"])     : "");
        $nomorsales  = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
        $kodesales = (isset($jsonObject["kodesales"]) ? $this->clean($jsonObject["kodesales"])     : "");
        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");
        $subtotaljasa = (isset($jsonObject["subtotaljasa"]) ? $this->clean($jsonObject["subtotaljasa"])     : "");
        $subtotalbiaya = (isset($jsonObject["subtotalbiaya"]) ? $this->clean($jsonObject["subtotalbiaya"])     : "");
        $disc = (isset($jsonObject["disc"]) ? $this->clean($jsonObject["disc"])     : "0");
        $discnominal = (isset($jsonObject["discnominal"]) ? $this->clean($jsonObject["discnominal"])     : "0");
        $dpp = (isset($jsonObject["dpp"]) ? $this->clean($jsonObject["dpp"])     : "0");
        $ppn = (isset($jsonObject["ppn"]) ? $this->clean($jsonObject["ppn"])     : "0");
        $ppnnominal = (isset($jsonObject["ppnnominal"]) ? $this->clean($jsonObject["ppnnominal"])     : "");
        $total = (isset($jsonObject["total"]) ? $this->clean($jsonObject["total"])     : "");
        $totalrp = (isset($jsonObject["totalrp"]) ? $this->clean($jsonObject["totalrp"])     : "");
        $pembuat = (isset($jsonObject["pembuat"]) ? $this->clean($jsonObject["pembuat"])     : "");
        $nomorcabang = (isset($jsonObject["nomorcabang"]) ? $this->clean($jsonObject["nomorcabang"])     : "");
        $cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "");
        $valuta = (isset($jsonObject["valuta"]) ? $this->clean($jsonObject["valuta"])     : "");
        $kurs = (isset($jsonObject["kurs"]) ? $this->clean($jsonObject["kurs"])     : "");
        $jenispenjualan = (isset($jsonObject["jenispenjualan"]) ? $this->clean($jsonObject["jenispenjualan"])     : "");
        $isbarangimport = (isset($jsonObject["isbarangimport"]) ? $this->clean($jsonObject["isbarangimport"])     : "");
        $isppn = (isset($jsonObject["isppn"]) ? $this->clean($jsonObject["isppn"])     : "");
		
		$this->db->trans_begin();

        $query = "INSERT INTO thorderjual (Kode, Tanggal, NomorCustomer, KodeCustomer, NomorBroker, KodeBroker, NomorSales, KodeSales, SubTotal, SubtotalJasa, SubtotalBiaya, Disc, DiscNominal, DPP, PPN,
                  PPNNominal, Total, TotalRp, Pembuat, NomorCabang, Cabang, Valuta, Kurs, JenisPenjualan, IsBarangImport, IsPPN)
                  VALUES ($kode, $tanggal, $nomorcustomer, $kodecustomer, $nomorbroker, $kodebroker, $nomorsales, $kodesales, $subtotal, $subtotaljasa, $subtotalbiaya, $disc, $discnominal, $dpp, $ppn,
                  $ppnnominal, $total, $totalrp, $pembuat, $nomorcabang, $cabang, $valuta, $kurs, $jenispenjualan, $isbarangimport, $isppn)";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        $nomordetail = (isset($jsonObject["nomordetail"]) ? $this->clean($jsonObject["nomordetail"])     : "");
        $nomorheader = (isset($jsonObject["nomorheader"]) ? $this->clean($jsonObject["nomorheader"])     : "");
        $nomorbarang = (isset($jsonObject["nomorbarang"]) ? $this->clean($jsonObject["nomorbarang"])     : "");
        $kodebarang = (isset($jsonObject["kodebarang"]) ? $this->clean($jsonObject["kodebarang"])     : "");
        $qty = (isset($jsonObject["qty"]) ? $this->clean($jsonObject["qty"])     : "");
        $jumlah = (isset($jsonObject["jumlah"]) ? $this->clean($jsonObject["jumlah"])     : "");
        $harga = (isset($jsonObject["harga"]) ? $this->clean($jsonObject["harga"])     : "");
        $fee = (isset($jsonObject["fee"]) ? $this->clean($jsonObject["fee"])     : "");
        $hargamandor = (isset($jsonObject["hargamandor"]) ? $this->clean($jsonObject["hargamandor"])     : "");
        $disc1 = (isset($jsonObject["disc1"]) ? $this->clean($jsonObject["disc1"])     : "");
        $disc1nominal = (isset($jsonObject["disc1nominal"]) ? $this->clean($jsonObject["disc1nominal"])     : "");
        $netto = (isset($jsonObject["netto"]) ? $this->clean($jsonObject["netto"])     : "");
        $subtotald = (isset($jsonObject["subtotald"]) ? $this->clean($jsonObject["subtotald"])     : "");
        $nomorpekerjaan = (isset($jsonObject["nomorpekerjaan"]) ? $this->clean($jsonObject["nomorpekerjaan"])     : "");
        $kodepekerjaan = (isset($jsonObject["kodepekerjaan"]) ? $this->clean($jsonObject["kodepekerjaan"])     : "");
        $nomorbarangjual = (isset($jsonObject["nomorbarangjual"]) ? $this->clean($jsonObject["nomorbarangjual"])     : "");
        $kodebarangjual = (isset($jsonObject["kodebarangjual"]) ? $this->clean($jsonObject["kodebarangjual"])     : "");
        $keterangandetail = (isset($jsonObject["keterangandetail"]) ? $this->clean($jsonObject["keterangandetail"])     : "");

        $query = "INSERT INTO tdorderjual (Nomor, NomorHeader, NomorBarang, KodeBarang, Qty, Jumlah, Harga, Fee,
                  HargaMandor, Disc1, DiscNominal, Netto, Subtotal, NomorPekerjaan, KodePekerjaan, NomorBarangJual, KodeBarangJual, KeteranganDetail)
                  VALUES ($nomordetail, $nomorheader, $nomorbarang, $kodebarang, $qty, $jumlah, $harga, $fee, $hargamandor, $disc1, $disc1nominal, $netto, $subtotald,
                  $nomorpekerjaan, $kodepekerjaan, $nomorbarangjual, $kodebarangjual, $keterangandetail)";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }
        else
        {
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
	
	function getOmzetTarget_post(){
		$data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
		$kodesales = (isset($jsonObject["kodesales"]) ? $this->clean($jsonObject["kodesales"])     : "a");
        $omzet  = $this->db->query(
					"SELECT SUM(a.TotalRp) omzet
					FROM thnotajual a
						LEFT JOIN thsales c
							ON a.nomorsales = c.nomor
						LEFT JOIN tcustomer b
							ON a.nomorcustomer = b.nomor
					WHERE a.status <> 0
						AND a.jenis = 'fj'
						AND a.approve = 1
						AND c.nomor = $kodesales ")->row()->omzet;
        $target = $this->db->query(
					"SELECT
					  decTarget target
					FROM
					  whtarget_mobile
					WHERE
					  intNomorMSales = $kodesales
					  AND intPeriode = MONTH(NOW())
					  AND intTahun = YEAR(NOW())")->row()->target;
        
        array_push($data['data'], array( 
										'success'   => "true",
    									'omzet' 	=> $omzet, 
    									'target'	=> $target
        								)
        );

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
	}
	
	// --- POST get periode --- //
	function getPeriode_post()
	{     
        $data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));
		
		$query = "	SELECT DISTINCT 
						intPeriode periode,
						intTahun AS tahun
					FROM whtarget_mobile
					ORDER BY intPeriode DESC;";
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                								'periode'					=> $r['periode'],
												'tahun' 					=> $r['tahun']
                								)
               	);
            }
        }else{		
			array_push($data['data'], array( 'query' => $this->error($query) ));
		}  
	
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
	
	// made by Shodiq @3-aug-2017
    // insert GPS history to data base without returning any value (no need to check)
    // if it got error, the data will gets rollbacked
    // if nothing went wrong, the data gets commited
    function pushTrackingData_post()
    {
		$data['data'] = array();
		
		$value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        
        $nomortuser		= (isset($jsonObject["nomortuser"])		? $jsonObject["nomortuser"]		: "0");
        $nomorthsales	= (isset($jsonObject["nomorthsales"])	? $jsonObject["nomorthsales"]	: "0");
        $latitude		= (isset($jsonObject["latitude"])		? $jsonObject["latitude"]		: "0");
        $longitude		= (isset($jsonObject["longitude"])		? $jsonObject["longitude"]		: "0");
        $fakeGPS		= (isset($jsonObject["fakeGPS"])		? $jsonObject["fakeGPS"]		: "0");
        
        $this->db->trans_begin();
        
        $query = "INSERT INTO `gms`.`whtracking_mobile`(`nomortuser`,`nomorthsales`,`latitude`,`longitude`,`trackingDate`,`fakeGPS`) VALUES ($nomortuser, $nomorthsales, $latitude, $longitude, NOW(), $fakeGPS)";
		
        $this->db->query($query);
        
        if ($this->db->trans_status() === FALSE)
		{
			$this->db->trans_rollback();
			array_push($data['data'], array( 'query' => $this->error($query) ));	
		}
		else
		{
			$this->db->trans_commit();
			array_push($data['data'], array( 'success' => 'true' ));
		}
		
		if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
	}

	function setTarget_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $rawdata = (isset($jsonObject["rawdata"]) ? $jsonObject["rawdata"]     : "a");
        $newdata = explode('|',$rawdata);
        $query = '';

        $this->db->trans_begin();
        for ($i = 0; $i < count($newdata); $i++) {
            if($newdata[$i] != ''){
                $strdata = explode('~', $newdata[$i]);
                $nomorsales = $strdata[0];
                $periode    = $strdata[1];
                $tahun      = $strdata[2];
                $target     = $strdata[3];
                $status     = 1;

                $query = "	INSERT INTO
                                whtarget_mobile (intNomorMsales, intPeriode, intTahun, decTarget, intStatus)
                            VALUES ($nomorsales, $periode, $tahun, $target, $status);";
                $result = $this->db->query($query);

                if ($this->db->trans_status() === FALSE){
                    $this->db->trans_rollback();
                    array_push($data['data'], array( 'success' => $newdata ));
                    break;
                }
            }

        }
        if ($this->db->trans_status() === FALSE){
            $this->db->trans_rollback();
            array_push($data['data'], array( 'success' => $query ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => "true", 'query' => $query));
        }
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function getSalesmanMonthly_post(){
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $periode = (isset($jsonObject["periode"]) ? $this->clean($jsonObject["periode"])     : "1");
        $tahun = (isset($jsonObject["tahun"]) ? $this->clean($jsonObject["tahun"])     : "1");
        $query = "  SELECT a.nomor nomor, a.nama nama, b.decTarget target FROM thsales a JOIN whtarget_mobile b ON b.intNomorMSales = a.nomor WHERE b.intPeriode = $periode AND b.intTahun = $tahun; ";
        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'success'				=> "true",
                                                'nomor'  				=> $r['nomor'],
                                                'nama'  				=> $r['nama'],
                                                'target'				=> $r['target']
                                                )
                );
            }
        }else{
            //array_push($data['data'], array( 'query' => $this->error($query) ));
            array_push($data['data'], array( 'success' => "false", 'query' => $this->error($query)));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
    
    function getTrackingData_post() {
		$data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));
		
		$query = "SELECT ";
		
		$isUsers	= (isset($jsonObject["isUsers"])		? $jsonObject["isUsers"]		: "0");
		
		if ($isUsers == true) {
			$query = $query . "user.nomortuser as nomoruser, user.userid as userid
				FROM whrole_mobile role, whuser_mobile user
				WHERE role.nama = 'SALES' and role.nomor = user.nomorrole;
			";
		} else {
			$nomortuser		= (isset($jsonObject["nomortuser"])		? $jsonObject["nomortuser"]		: "0");
			$startFilter	= (isset($jsonObject["startFilter"])	? $jsonObject["startFilter"]	: "");
			$endFilter		= (isset($jsonObject["endFilter"])		? $jsonObject["endFilter"]		: "");
			$query = $query . "latitude, longitude, trackingDate FROM whtracking_mobile WHERE nomortuser = $nomortuser";
			if ($startFilter != "") {
				$query = $query . " and trackingDate >= str_to_date('$startFilter', '%d %M %Y')";
				$query = $query . " and trackingDate <= str_to_date('$endFilter', '%d %M %Y')";
			} else {
				$query = $query . " order by nomor desc limit 1";
			}
		}
					
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
			if ($isUsers == true) {
				foreach ($result->result_array() as $r){
					array_push($data['data'], array(
													'nomoruser'	=> $r['nomoruser'],
													'userid' 	=> $r['userid']
													)
					);
				}
			} else {
				foreach ($result->result_array() as $r){
					array_push($data['data'], array(
													'latitude' 		=> $r['latitude'],
													'longitude' 	=> $r['longitude'],
													'trackingDate' 	=> $r['trackingDate']
													)
					);
				}
            }
        }else{		
			array_push($data['data'], array( 'query' => $this->error($query) ));
		}  
	
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
	}

    // added by Tonny
    // --- POST get salesman omzet pada bulan ini atau tahun ini--- //
    function getSalesOmzet_post() {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $nomorsales = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
        $bulantahun = (isset($jsonObject["bulantahun"]) ? $this->clean($jsonObject["bulantahun"])     : "bulan");
        $enddate = (isset($jsonObject["enddate"]) ? $this->clean($jsonObject["enddate"])     : "");

        $query = "	SELECT a.NomorSales nomorsales, b.nama namacustomer, c.nama namasales, a.TotalRp omzet, a.tanggal tanggal
                    FROM thnotajual a
                        LEFT JOIN tcustomer b
                            ON a.nomorcustomer = b.nomor
                        LEFT JOIN thsales c
                            ON a.nomorsales = c.nomor
                    WHERE a.status <> 0
                        AND a.jenis = 'fj'
                        AND a.approve = 1
                        AND YEAR(a.tanggal) = YEAR('$enddate')
                        AND a.tanggal <= '$enddate' ";

        if($nomorsales != ''){
            $query = $query . " AND c.nomor = $nomorsales ";
        }
        if($bulantahun == 'bulan'){
            $query = $query . " AND MONTH(a.tanggal) = MONTH('$enddate') ";
        }
        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'nomorsales'			=> $r['nomorsales'],
                                                'namasales'			    => $r['namasales'],
                                                'omzet'					=> $r['omzet'],
                                                'tanggal'			    => $r['tanggal'],
                                                'namacustomer'			=> $r['namacustomer']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
            //array_push($data['data'], array( 'success' => "false" ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    //untuk mendapatkan value dari total omzet
    function getTotalOmzet_post() {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $nomorsales = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
        $bulantahun = (isset($jsonObject["bulantahun"]) ? $this->clean($jsonObject["bulantahun"])     : "bulan");
        $enddate = (isset($jsonObject["enddate"]) ? $this->clean($jsonObject["enddate"])     : "");

        $querytotalomzet = "SELECT SUM(a.TotalRp) totalomzet
                            FROM thnotajual a
                                LEFT JOIN thsales c
                                    ON a.nomorsales = c.nomor
                                LEFT JOIN tcustomer b
                                    ON a.nomorcustomer = b.nomor
                            WHERE a.status <> 0
                                AND a.jenis = 'fj'
                                AND a.approve = 1
                                AND YEAR(a.tanggal) = YEAR('$enddate')
                                AND a.tanggal <= '$enddate' ";
        if($nomorsales != ''){
            $querytotalomzet = $querytotalomzet . " AND c.nomor = $nomorsales ";
        }
        if($bulantahun == 'bulan'){
            $querytotalomzet = $querytotalomzet . " AND MONTH(a.tanggal) = MONTH('$enddate') ";
        }
        $totalomzet  = $this->db->query($querytotalomzet)->row()->totalomzet;

        array_push($data['data'], array(
                                        'totalomzet'		=> $totalomzet
                                        )
        );

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

}