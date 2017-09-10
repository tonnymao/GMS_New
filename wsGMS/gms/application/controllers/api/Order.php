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
class Order extends REST_Controller {

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

    //added by Tonny
    // untuk mendapatkan format setting untuk sales order
    //output -> [prefix],[length nomor urut],[format bulan tahun],[header transaksi],[detail transaksi] -> 'SOP,5,YYMM,TTRANSAKSI,TDORDERJUAL'
    function getFormatSettingSalesOrder_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "219");
        $query = "SELECT formatsetting FROM tformatsetting WHERE nomor = $nomor";
        $result = $this->db->query($query);
        if($result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'formatsetting'			=> $r['formatsetting']
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

    //added by Tonny
    //untuk mendapatkan nomor baru untuk salesorder header
    function getCounterHeader_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $prefix = (isset($jsonObject["prefix"]) ? $this->clean($jsonObject["prefix"])     : "SOP");  //input merupakan kodetransaksi
        $query = "SELECT MAX(SUBSTR(kode, LOCATE('/', kode, 8) + 1)) AS counter FROM thorderjual WHERE kode LIKE '%$prefix%'";
        //$query = "SELECT MAX(kode) as lastkode FROM thorderjual WHERE kode like '%$prefix%''";
        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'counter'	=> $r['counter']
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

    //added by Tonny
    //untuk mendapatkan nomor baru untuk salesorder detail
    function getCounterDetail_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $kode = (isset($jsonObject["kode"]) ? $this->clean($jsonObject["kode"])     : "tdorderjual");  //input merupakan kodetransaksi
        $query = "SELECT akhir counter FROM tcount WHERE kode = '$kode'";
        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'counter'	=> $r['counter']
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
}
