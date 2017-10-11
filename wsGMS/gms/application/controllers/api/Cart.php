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
class Cart extends REST_Controller {

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
	
	function getGCMId($user_nomor){
        $query = "  SELECT 
                    a.gcmid
                    FROM whuser_mobile a 
                    WHERE a.status_aktif > 0 AND (a.gcmid <> '' AND a.gcmid IS NOT NULL) AND a.nomor = $user_nomor ";
        return $this->db->query($query)->row()->gcmid;
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
	
	function test_get()
	{
		
		$result = "a";
		
		$data['data'] = array();
		
		// START SEND NOTIFICATION
        $vcGCMId = $this->getGCMId(3);
		
        $this->send_gcm($vcGCMId, $this->ellipsis('$new_message'),'New Message(s) From ','PrivateMessage','0','0');
		
		$this->response($vcGCMId);
		
		/*
		$regisID = array();
			
		$query_getuser = " SELECT 
							a.gcmid
							FROM whuser_mobile a 
							JOIN whrole_mobile b ON a.nomorrole = b.nomor
							WHERE a.status_aktif > 0 AND (a.gcmid <> '' AND a.gcmid IS NOT NULL) AND b.approveberitaacara = 1 ";
		$result_getuser = $this->db->query($query_getuser);

		if( $result_getuser && $result_getuser->num_rows() > 0){
			foreach ($result_getuser->result_array() as $r_user){

				// START SEND NOTIFICATION
				$vcGCMId = $r_user['gcmid'];
				if( $vcGCMId != "null" ){      
					array_push($regisID, $vcGCMId);       
				}
				
			}
			$count = $this->db->query("SELECT COUNT(1) AS elevasi_baru FROM mhberitaacara a WHERE a.status_disetujui = 0")->row()->elevasi_baru; 
			$this->send_gcm_group($regisID, $this->ellipsis("Berita Acara Elevasi"),$count . ' pending elevasi','ChooseApprovalElevasi','','');
		} 
		*/
	}

    // added by Tonny
	// --- POST get barang pada shopping cart --- //
	function getBarang_post()
	{     
        $data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));
		$nomorcustomer = (isset($jsonObject["nomorcustomer"]) ? $this->clean($jsonObject["nomorcustomer"]) : "");
		$query = "	SELECT 
						c.nomorbarang AS nomor,
						c.kodebarang AS kode,
						a.nama AS nama,
						a.NamaJual AS namajual,
					    c.jumlah AS jumlah,
						b.satuan AS satuan,
						a.HargaJualIDR AS hargajual,
						c.subtotal
					FROM tbarang a
					JOIN vwbarang b ON a.nomor = b.nomor
					JOIN tdcart c ON a.nomor = c.nomorbarang
					JOIN thcart d ON d.nomor = c.nomorheader
					WHERE
					    a.aktif = 1
					AND
					    d.nomorcustomer = $nomorcustomer
					ORDER BY a.nama DESC;";
        $result = $this->db->query($query);

        if($result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                								'nomor'					=> $r['nomor'],
                								'namajual' 				=> $r['namajual'],
												'kode' 					=> $r['kode'],
                								'satuan' 				=> $r['satuan'],
                								'hargajual' 			=> $r['hargajual'],
                								'jumlah' 			    => $r['jumlah'],
                								'subtotal'   			=> $r['subtotal']
                								));
            }
        }else{
			array_push($data['data'], array( 'query' => $this->error($query) ));
		}
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function addToCart_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $nomorcustomer = (isset($jsonObject["nomorcustomer"]) ? $this->clean($jsonObject["nomorcustomer"]) : "");
        $kodecustomer = (isset($jsonObject["kodecustomer"]) ? $this->clean($jsonObject["kodecustomer"]) : "");
        $grandtotal = (isset($jsonObject["grandtotal"]) ? $this->clean($jsonObject["grandtotal"]) : "");
        $cart = (isset($jsonObject["cart"]) ? ($jsonObject["cart"]) : "");
        $pieces = explode("|", $cart);

        if(count($pieces) > 1){
            $this->db->trans_begin();
            //pengecekan jika sudah ada data sebelumnya
            $query = "  SELECT
                          a.nomor as nomor
                        FROM thcart a
                        WHERE a.nomorcustomer = $nomorcustomer ";
            $result = $this->db->query($query);
            //get nomorheader
            $nomorheader = $result->row()->nomor;
            if($result){
                if($result->num_rows() > 0){
                    $query = "  UPDATE thcart SET subtotal = $grandtotal WHERE nomorcustomer = $nomorcustomer";
                    $result = $this->db->query($query);
                }else{
                    //jika user belum memiliki pada thcart, maka buat thcart dulu sebelum melakukan insert pada tdcart
                    $query = "	INSERT INTO thcart (tanggal, nomorcustomer, kodecustomer, subtotal, aktif)
                             VALUES (NOW(), $nomorcustomer, '$kodecustomer', $grandtotal, 1)";
                    $result = $this->db->query($query);
                    if($result){
                        $query = "  SELECT
                                      a.nomor as nomor
                                    FROM thcart a
                                    WHERE a.nomorcustomer = $nomorcustomer ";
                        $result = $this->db->query($query);
                        //get nomorheader
                        $nomorheader = $result->row()->nomor;
                    }else{
                        $this->db->trans_rollback();
                        array_push($data['data'], array( 'query' => $this->error($query) ));
                        if ($data){
                            // Set the response and exit
                            $this->response($data['data']); // OK (200) being the HTTP response code
                        }
                        die;
                    }
                }
                if($result){  //jika berhasil insert/update ke thcart
                    //nomorbarang~namajual~kodebarang~satuan~harga~jumlah~subtotal
                    for($i = 0; $i < count($pieces) - 1; $i++){
                        $parts = explode("~", $pieces[$i]);
                        $nomorbarang = $parts[0];
                        $kodebarang = $parts[2];
                        $harga = $parts[4];
                        $jumlah = $parts[5];
                        $subtotal = $parts[6];
                        //cek jika user sudah memiliki barang yg sama pada tdcart
                        $query = "  SELECT
                                      b.nomorbarang
                                    FROM thcart a
                                    JOIN tdcart b
                                      ON a.nomor = b.nomorheader
                                    WHERE a.nomorcustomer = $nomorcustomer
                                      AND b.nomorbarang = $nomorbarang ";
                        $result = $this->db->query($query);
                        if($result && $result->num_rows() > 0){
                            $query = "  UPDATE tdcart SET jumlah = jumlah + $jumlah,
                                          subtotal = jumlah * harga
                                        WHERE nomorbarang = $nomorbarang
                                          AND nomorheader = $nomorheader ";
                            $result = $this->db->query($query);
                            if($result){
                                $this->db->trans_commit();
                                array_push($data['data'], array( 'success' => "true"));
                            }else{
                                $this->db->trans_rollback();
                                array_push($data['data'], array( 'query' => $this->error($query) ));
                                if ($data){
                                    // Set the response and exit
                                    $this->response($data['data']); // OK (200) being the HTTP response code
                                }
                                die;
                            }
                        }else{
                            $query = "	INSERT INTO tdcart (nomorheader, nomorbarang, kodebarang, jumlah, harga, subtotal, aktif)
                                        VALUES ($nomorheader, $nomorbarang, '$kodebarang', $jumlah, $harga, $subtotal, 1)";
                            $result = $this->db->query($query);
                            if(!$result){
                                $this->db->trans_rollback();
                                array_push($data['data'], array( 'query' => $this->error($query) ));
                                if ($data){
                                    // Set the response and exit
                                    $this->response($data['data']); // OK (200) being the HTTP response code
                                }
                                die;
                            }
                        }
                    }
                    $this->db->trans_commit();
                    array_push($data['data'], array( 'success' => "true"));
                }
            }
        }else{
            array_push($data['data'], array( 'query' => 'no data to save'));
        }
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
            //nomorbarang~namajual~kodebarang~satuan~harga~jumlah~subtotal
//            for($i = 0; $i < count($pieces); $i++){
//                $parts = explode("~", $pieces[$i]);
//                $nomorbarang = $parts[0];
//                $kodebarang = $parts[2];
//                $harga = $parts[4];
//                $jumlah = $parts[5];
//                $subtotal = $parts[6];
//                //cek jika user sudah memiliki data pada thcart dan barang yg sama pada cart
//                $query = "  SELECT
//                              a.nomor,
//                              b.nomorbarang
//                            FROM thcart a
//                            JOIN tdcart b
//                              ON a.nomor = b.nomorheader
//                            WHERE a.nomorcustomer = $nomorcustomer
//                              AND b.nomorbarang = $nomorbarang ";
//                $result = $this->db->query($query);
//                if($result && $result->num_rows() > 0){  //jika user memiliki data dan barang yg sama pada cart, maka update subtotal dan jumlah barang pada thcart
//                    //get nomorheader
//                    $nomorheader = $result->row()->nomor;
//                    //update subtotal pada thcart
//                    $query = "  UPDATE thcart SET subtotal = $subtotal WHERE nomorcustomer = $nomorcustomer";
//                    $result = $this->db->query($query);
//                    if($result){
//                        //update jumlah dan subtotal barang pada tdcart
//                        $query = "  UPDATE tdcart SET jumlah = jumlah + $jumlah,
//                                      subtotal = jumlah * harga
//                                    WHERE nomorbarang = $nomorbarang
//                                      AND nomorheader = $nomorheader ";
//                        $result = $this->db->query($query);
//                        if($result){
//                            $this->db->trans_commit();
//                            array_push($data['data'], array( 'success' => "true"));
//                        }else{
//                            $this->db->trans_rollback();
//                            array_push($data['data'], array( 'query' => $this->error($query) ));
//                        }
//                    }else{
//                        $this->db->trans_rollback();
//                        array_push($data['data'], array( 'query' => $this->error($query) ));
//                    }
//                }else{  //jika user belum memiliki pada thcart, maka buat thcart dulu sebelum melakukan insert pada tdcart
//                    $query = "	INSERT INTO thcart (tanggal, nomorcustomer, kodecustomer, subtotal, aktif)
//                             VALUES (NOW(), $nomorcustomer, '$kodecustomer', $grandtotal, 1)";
//                    $result = $this->db->query($query);
//                    if($result){
//                        $query = "	INSERT INTO tdcart (nomorheader, nomorbarang, kodebarang, jumlah, harga, subtotal, aktif)
//                                    VALUES ($nomorheader, $nomorbarang, '$kodebarang', $jumlah, $harga, $subtotal, 1)";
//                        $result = $this->db->query($query);
//                        if($result){
//                            $this->db->trans_commit();
//                            array_push($data['data'], array( 'success' => "true"));
//                        }
//                    }else{
//                        $this->db->trans_rollback();
//                        array_push($data['data'], array( 'query' => $this->error($query) ));
//                    }
//                }
//            }
//        }else{
//            array_push($data['data'], array( 'query' => 'no data to save'));
//        }

    }

    function explode_get(){
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $nomorcustomer = (isset($jsonObject["nomorcustomer"]) ? $this->clean($jsonObject["nomorcustomer"]) : "");
        $kodecustomer = (isset($jsonObject["kodecustomer"]) ? $this->clean($jsonObject["kodecustomer"]) : "");
        $cart = (isset($jsonObject["cart"]) ? $this->clean($jsonObject["cart"]) : "");
        $pieces = explode("|", $cart);

        if(count($pieces) > 1){}
        array_push($data['data'], array( 'query' => $this->error($query) ));
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }
}
