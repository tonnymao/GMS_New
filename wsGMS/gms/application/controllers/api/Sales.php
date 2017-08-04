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

	// --- POST get contact --- //
	function getOmzet_post()
	{     
        $data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));
		
		$kodesales = (isset($jsonObject["kodesales"]) ? $this->clean($jsonObject["kodesales"])     : "a");
		
		$query = "	SELECT SUM(a.TotalRp) omzet
					FROM thnotajual a
						LEFT JOIN thsales c
							ON a.nomorsales = c.nomor
						LEFT JOIN tcustomer b
							ON a.nomorcustomer = b.nomor
					WHERE a.status <> 0 
						AND a.jenis = 'fj' 
						AND a.approve = 1
						AND c.nomor = ? ";
        $result = $this->db->query($query, array($kodesales));
		//$result = $this->db->query($query, array($user, $pass));

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
												'success'				=> "true",
                								'omzet'					=> $r['omzet']
                								)
               	);
            }
        }else{		
			//array_push($data['data'], array( 'query' => $this->error($query) ));
			array_push($data['data'], array( 'success' => "false" ));
		}  
	
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }
	
	// --- POST get barang --- //
	function getTarget_post()
	{     
        $data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));
		
		$kodesales = (isset($jsonObject["kodesales"]) ? $this->clean($jsonObject["kodesales"])     : "a");
		
		$query = "	SELECT
					  decTarget target
					FROM
					  whtarget_mobile
					WHERE
					  intNomorMSales = ?
					  AND intPeriode = MONTH(NOW())
					  AND intTahun = YEAR(NOW())";
        $result = $this->db->query($query, array($kodesales));
		//$result = $this->db->query($query, array($user, $pass));

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
												'success'				=> "true",
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
}
