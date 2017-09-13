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

    //added by Tonny
    //untuk mendapatkan nomor salesorder header yg sekarang
    function getNomorCountHeader_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $kodecount = (isset($jsonObject["kodecount"]) ? $this->clean($jsonObject["kodecount"])     : "");
        $query = "SELECT akhir counter FROM tcount WHERE kode = '$kodecount'";
        $result = $this->db->query($query);
        if($result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'counter'		=> $r['counter']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }
    }

//	// --- POST insert new order jual --- //
//	//added by Tonny
//	function insertNewOrderJual_post()
//	{
//        $data['data'] = array();
//
//        $value = file_get_contents('php://input');
//		$jsonObject = (json_decode($value , true));
//        ////////////////////////////////////////////////////////////////////////////////HEADER/////////////////////////////////////////////////////////////////////////////
//		$headerkode = (isset($jsonObject["headerkode"]) ? $this->clean($jsonObject["headerkode"])     : "");  // berisi string 'ttransaksi'
//        //$nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "");
//        $nomor = $this->getNomorHeader($headerkode);
//		$kode = (isset($jsonObject["kode"]) ? $this->clean($jsonObject["kode"]) : "");
//        $tanggal = (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
//        $nomorcustomer = (isset($jsonObject["nomorcustomer"]) ? $this->clean($jsonObject["nomorcustomer"])     : "");
//        $kodecustomer = (isset($jsonObject["kodecustomer"]) ? $this->clean($jsonObject["kodecustomer"])     : "");
//        $nomorbroker = (isset($jsonObject["nomorbroker"]) ? $this->clean($jsonObject["nomorbroker"])     : "");
//        $kodebroker = (isset($jsonObject["kodebroker"]) ? $this->clean($jsonObject["kodebroker"])     : "");
//        $nomorsales  = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
//        $kodesales = (isset($jsonObject["kodesales"]) ? $this->clean($jsonObject["kodesales"])     : "");
//        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");
//        $subtotaljasa = (isset($jsonObject["subtotaljasa"]) ? $this->clean($jsonObject["subtotaljasa"])     : "");
//        $subtotalbiaya = (isset($jsonObject["subtotalbiaya"]) ? $this->clean($jsonObject["subtotalbiaya"])     : "");
//        $disc = (isset($jsonObject["disc"]) ? $this->clean($jsonObject["disc"])     : "0");
//        $discnominal = (isset($jsonObject["discnominal"]) ? $this->clean($jsonObject["discnominal"])     : "0");
//        $dpp = (isset($jsonObject["dpp"]) ? $this->clean($jsonObject["dpp"])     : "0");
//        $ppn = (isset($jsonObject["ppn"]) ? $this->clean($jsonObject["ppn"])     : "0");
//        $ppnnominal = (isset($jsonObject["ppnnominal"]) ? $this->clean($jsonObject["ppnnominal"])     : "");
//        $total = (isset($jsonObject["total"]) ? $this->clean($jsonObject["total"])     : "");
//        $totalrp = (isset($jsonObject["totalrp"]) ? $this->clean($jsonObject["totalrp"])     : "");
//        $pembuat = (isset($jsonObject["pembuat"]) ? $this->clean($jsonObject["pembuat"])     : "");
//        $nomorcabang = (isset($jsonObject["nomorcabang"]) ? $this->clean($jsonObject["nomorcabang"])     : "");
//        $cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "");
//        $valuta = (isset($jsonObject["valuta"]) ? $this->clean($jsonObject["valuta"])     : "");
//        $kurs = (isset($jsonObject["kurs"]) ? $this->clean($jsonObject["kurs"])     : "");
//        $jenispenjualan = (isset($jsonObject["jenispenjualan"]) ? $this->clean($jsonObject["jenispenjualan"])     : "");
//        $isbarangimport = (isset($jsonObject["isbarangimport"]) ? $this->clean($jsonObject["isbarangimport"])     : "");
//        $isppn = (isset($jsonObject["isppn"]) ? $this->clean($jsonObject["isppn"])     : "");
//
//		$this->db->trans_begin();
//
//        $query = "INSERT INTO thorderjual (Nomor, Kode, Tanggal, NomorCustomer, KodeCustomer, NomorBroker, KodeBroker, NomorSales, KodeSales, SubTotal, SubtotalJasa, SubtotalBiaya, Disc, DiscNominal, DPP, PPN,
//                  PPNNominal, Total, TotalRp, Pembuat, NomorCabang, Cabang, Valuta, Kurs, JenisPenjualan, IsBarangImport, IsPPN)
//                  VALUES ($nomor, '$kode', '$tanggal', $nomorcustomer, '$kodecustomer', $nomorbroker, '$kodebroker', $nomorsales, '$kodesales', $subtotal, $subtotaljasa, $subtotalbiaya, $disc, $discnominal, $dpp, $ppn,
//                  $ppnnominal, $total, $totalrp, '$pembuat', $nomorcabang, '$cabang', '$valuta', $kurs, '$jenispenjualan', $isbarangimport, $isppn)";
//
//        $this->db->query($query);
//
//        if ($this->db->trans_status() === FALSE)
//        {
//            $this->db->trans_rollback();
//            array_push($data['data'], array( 'query' => $this->error($query) ));
//        }else{
//            //counter ttransaksi + 1 jika insert berhasil dilakukan
//            $query = "UPDATE tcount set akhir = akhir + 1 WHERE kode = '$headerkode' ";
//            $this->db->query($query);
//            if ($this->db->trans_status() === FALSE)
//            {
//                $this->db->trans_rollback();
//                array_push($data['data'], array( 'query' => $this->error($query) ));
//                die();
//            }
//        }
//
//        ////////////////////////////////////////////////////////////////////////////////DETAIL/////////////////////////////////////////////////////////////////////////////
//        $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $this->clean($jsonObject["dataitemdetail"])     : "");
//        $datapekerjaandetail = (isset($jsonObject["datapekerjaandetail"]) ? $this->clean($jsonObject["datapekerjaandetail"])     : "");
//        $detailkode = (isset($jsonObject["detailkode"]) ? $this->clean($jsonObject["detailkode"])     : "");  // berisi string 'tdorderjual'
//
//        $pieces = explode('|', $dataitemdetail);
//        if (count($pieces) > 1){
//            for ($i = 0; $i < count($pieces); $i++) {
//                $parts = explode("~", $pieces[i]);  //nomorbarang~kodebarang~namabarang~satuan~harga~qty~fee~disc~subtotal~notes
//                //tambah count +1
//                $nomordetail = $this->getNomorDetail($detailkode);
//                $nomorheader = $nomor;
//                $nomorbarang = $parts[0];
//                $kodebarang = $parts[1];
//                $qty = $parts[5];
//                $jumlah = $parts[5];
//                $harga = $parts[4];
//                $fee = $parts[6];
//                $hargamandor = parts[6];
//                $disc1 = parts[7];
//                $disc1nominal = $harga * $disc1 / 100 ;
//                $netto = $harga - $disc1nominal + $fee;
//                $subtotald = parts[8];
//                $nomorbarangjual = $nomorbarang;
//                $kodebarangjual = $kodebarang;
//                $keterangandetail = parts[9];
//
//                //insert barang
//                $query = "INSERT INTO tdorderjual (Nomor, NomorHeader, NomorBarang, KodeBarang, Qty, Jumlah, Harga, Fee,
//                          HargaMandor, Disc1, DiscNominal, Netto, Subtotal, NomorBarangJual, KodeBarangJual, KeteranganDetail)
//                          VALUES ($nomordetail, $nomorheader, $nomorbarang, '$kodebarang', $qty, $jumlah, $harga, $fee, $hargamandor, $disc1, $disc1nominal, $netto, $subtotald,
//                          $nomorbarangjual, '$kodebarangjual', '$keterangandetail')";
//
//                $this->db->query($query);
//
//                if ($this->db->trans_status() === FALSE)
//                {
//                    $this->db->trans_rollback();
//                    array_push($data['data'], array( 'query' => $this->error($query) ));
//                }else{
//                    //count + 1 jika insert berhasil dilakukan
//                    $query = "UPDATE tcount set akhir = akhir + 1 WHERE kode = '$detailkode' ";
//                    $this->db->query($query);
//                    if ($this->db->trans_status() === FALSE)
//                    {
//                        $this->db->trans_rollback();
//                        array_push($data['data'], array( 'query' => $this->error($query) ));
//                    }
//                }
//            }
//        }
//
//        $pieces = explode('|', $datapekerjaandetail);
//        if (count($pieces) > 1){
//            for ($i = 0; $i < count($pieces); $i++) {
//                $parts = explode("~", $pieces[i]);  //nomorbarang~kodebarang~namabarang~satuan~harga~qty~fee~disc~subtotal~notes
//                //tambah count +1
//                $nomordetail = $this->getNomorDetail($detailkode);
//                $nomorheader = $nomor;
//                $nomorpekerjaan = $parts[0];
//                $kodepekerjaan = $parts[1];
//                $qty = $parts[5];
//                $jumlah = $parts[5];
//                $harga = $parts[4];
//                $fee = $parts[6];
//                $hargamandor = parts[6];
//                $disc1 = parts[7];
//                $disc1nominal = $harga * $disc1 / 100 ;
//                $netto = $harga - $disc1nominal + $fee;
//                $subtotald = parts[8];
//                $keterangandetail = parts[9];
//
//                //insert pekerjaan
//                $query = "INSERT INTO tdorderjual (Nomor, NomorHeader, NomorPekerjaan, KodePekerjaan, Qty, Jumlah, Harga, Fee,
//                          HargaMandor, Disc1, DiscNominal, Netto, Subtotal, KeteranganDetail)
//                          VALUES ($nomordetail, $nomorheader, $nomorpekerjaan, '$kodepekerjaan', $qty, $jumlah, $harga, $fee, $hargamandor, $disc1, $disc1nominal, $netto, $subtotald, '$keterangandetail')";
//
//                $this->db->query($query);
//
//                if ($this->db->trans_status() === FALSE)
//                {
//                    $this->db->trans_rollback();
//                    array_push($data['data'], array( 'query' => $this->error($query) ));
//                }else{
//                    //count + 1 jika insert berhasil dilakukan
//                    $query = "UPDATE tcount set akhir = akhir + 1 WHERE kode = '$detailkode' ";
//
//                    $this->db->query($query);
//                    if ($this->db->trans_status() === FALSE)
//                    {
//                        $this->db->trans_rollback();
//                        array_push($data['data'], array( 'query' => $this->error($query) ));
//                    }else{
//                        $this->db->trans_commit();
//                        array_push($data['data'], array( 'success' => 'true' ));
//                    }
//                }
//            }
//        }
//        if ($data){
//            // Set the response and exit
//            $this->response($data['data']); // OK (200) being the HTTP response code
//        }
//    }

	// --- POST insert new order jual --- //
	//added by Tonny
	function insertNewOrderJual_post()
	{
        $data['data'] = array();

        $value = file_get_contents('php://input');
		$jsonObject = (json_decode($value , true));
        ////////////////////////////////////////////////////////////////////////////////HEADER/////////////////////////////////////////////////////////////////////////////
		$headerkode = (isset($jsonObject["headerkode"]) ? $this->clean($jsonObject["headerkode"])     : "");  // berisi string 'ttransaksi'
        //$nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "");
        $nomor = $this->getNomorHeader($headerkode);
		$kode = (isset($jsonObject["kode"]) ? $this->clean($jsonObject["kode"]) : "");
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

        $query = "INSERT INTO thorderjual (Nomor, Kode, Tanggal, NomorCustomer, KodeCustomer, NomorBroker, KodeBroker, NomorSales, KodeSales, SubTotal, SubtotalJasa, SubtotalBiaya, Disc, DiscNominal, DPP, PPN,
                  PPNNominal, Total, TotalRp, Pembuat, NomorCabang, Cabang, Valuta, Kurs, JenisPenjualan, IsBarangImport, IsPPN)
                  VALUES ($nomor, '$kode', '$tanggal', $nomorcustomer, '$kodecustomer', $nomorbroker, '$kodebroker', $nomorsales, '$kodesales', $subtotal, $subtotaljasa, $subtotalbiaya, $disc, $discnominal, $dpp, $ppn,
                  $ppnnominal, $total, $totalrp, '$pembuat', $nomorcabang, '$cabang', '$valuta', $kurs, '$jenispenjualan', $isbarangimport, $isppn)";
        $this->db->query($query);

        $query = "UPDATE tcount set akhir = akhir + 1 WHERE kode = '$headerkode' ";
        $this->db->query($query);

        ////////////////////////////////////////////////////////////////////////////////DETAIL/////////////////////////////////////////////////////////////////////////////
        $noitem = FALSE;
        $detailkode = (isset($jsonObject["detailkode"]) ? $this->clean($jsonObject["detailkode"])     : "");  // berisi string 'tdorderjual'
        $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $this->clean($jsonObject["dataitemdetail"])     : "");
        $datapekerjaandetail = (isset($jsonObject["datapekerjaandetail"]) ? $this->clean($jsonObject["datapekerjaandetail"])     : "");

        $pieces = explode('|', $dataitemdetail);
        if (count($pieces) > 1){
            for ($i = 0; $i < count($pieces); $i++) {
                $parts = explode("~", $pieces[i]);  //nomorbarang~kodebarang~namabarang~satuan~harga~qty~fee~disc~subtotal~notes
                //tambah count +1
                $nomordetail = $this->getNomorDetail($detailkode);
                $nomorheader = $nomor;
                $nomorbarang = $parts[0];
                $kodebarang = $parts[1];
                $qty = $parts[5];
                $jumlah = $parts[5];
                $harga = $parts[4];
                $fee = $parts[6];
                $hargamandor = parts[6];
                $disc1 = parts[7];
                $disc1nominal = $harga * $disc1 / 100 ;
                $netto = $harga - $disc1nominal + $fee;
                $subtotald = parts[8];
                $nomorbarangjual = $nomorbarang;
                $kodebarangjual = $kodebarang;
                $keterangandetail = parts[9];

                //insert barang
                $query = "INSERT INTO tdorderjual (Nomor, NomorHeader, NomorBarang, KodeBarang, Qty, Jumlah, Harga, Fee,
                          HargaMandor, Disc1, DiscNominal, Netto, Subtotal, NomorBarangJual, KodeBarangJual, KeteranganDetail)
                          VALUES ($nomordetail, $nomorheader, $nomorbarang, '$kodebarang', $qty, $jumlah, $harga, $fee, $hargamandor, $disc1, $disc1nominal, $netto, $subtotald,
                          $nomorbarangjual, '$kodebarangjual', '$keterangandetail')";
                $this->db->query($query);

                if ($this->db->trans_status() === FALSE)
                {
                    $this->db->trans_rollback();
                    array_push($data['data'], array( 'query' => $this->error($query) ));
                }else{
                    $query = "UPDATE tcount set akhir = akhir + 1 WHERE kode = '$detailkode' ";
                    $this->db->query($query);
                }
            }
        }else{  //jika tidak ada item yg diinputkan maka lakukan rollback dan error
            $noitem = TRUE;
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        $pieces = explode('|', $datapekerjaandetail);
        if (count($pieces) > 1 && $noitem == FALSE){
            for ($i = 0; $i < count($pieces); $i++) {
                $parts = explode("~", $pieces[i]);  //nomorbarang~kodebarang~namabarang~satuan~harga~qty~fee~disc~subtotal~notes
                //tambah count +1
                $nomordetail = $this->getNomorDetail($detailkode);
                $nomorheader = $nomor;
                $nomorpekerjaan = $parts[0];
                $kodepekerjaan = $parts[1];
                $qty = $parts[5];
                $jumlah = $parts[5];
                $harga = $parts[4];
                $fee = $parts[6];
                $hargamandor = parts[6];
                $disc1 = parts[7];
                $disc1nominal = $harga * $disc1 / 100 ;
                $netto = $harga - $disc1nominal + $fee;
                $subtotald = parts[8];
                $keterangandetail = parts[9];

                //insert pekerjaan
                $query = "INSERT INTO tdorderjual (Nomor, NomorHeader, NomorPekerjaan, KodePekerjaan, Qty, Jumlah, Harga, Fee,
                          HargaMandor, Disc1, DiscNominal, Netto, Subtotal, KeteranganDetail)
                          VALUES ($nomordetail, $nomorheader, $nomorpekerjaan, '$kodepekerjaan', $qty, $jumlah, $harga, $fee, $hargamandor, $disc1, $disc1nominal, $netto, $subtotald, '$keterangandetail')";
                $this->db->query($query);

                if ($this->db->trans_status() === FALSE)
                {
                    $this->db->trans_rollback();
                    array_push($data['data'], array( 'query' => $this->error($query) ));
                }else{
                    $query = "UPDATE tcount set akhir = akhir + 1 WHERE kode = '$detailkode' ";
                    $this->db->query($query);
                }
            }
        }

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }
        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

	//untuk menampilkan data di salesorder list
	function getSalesOrderList_post(){
		$data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
		$nomorsales = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
		$approve = (isset($jsonObject["approve"]) ? $this->clean($jsonObject["approve"])     : "0");
		$kode = (isset($jsonObject["kode"]) ? $jsonObject["kode"]     : "");
		$cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "");

        $query = "SELECT a.Kode kode,
                  a.Tanggal tanggal,
                  a.NomorCabang nomorcabang,
                  a.Cabang cabang,
                  a.NomorCustomer nomorcustomer,
                  a.KodeCustomer kodecustomer,
                  b.nama namacustomer
                  FROM thorderjual a
                  JOIN tcustomer b
                    ON b.nomor = a.nomorcustomer
                  WHERE a.status = 1
                    AND a.nomorsales = '$nomorsales'
                    AND a.approve = $approve
                    AND a.NomorCabang = $cabang
                    AND a.kode REGEXP '$kode'";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'kode'					=> $r['kode'],
                                                'tanggal' 			    => $r['tanggal'],
                                                'nomorcabang' 			=> $r['nomorcabang'],
                                                'cabang' 			    => $r['cabang'],
                                                'nomorcustomer' 	    => $r['nomorcustomer'],
                                                'kodecustomer' 			=> $r['kodecustomer'],
                                                'namacustomer' 			=> $r['namacustomer']
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
        //$query = "SELECT MAX(SUBSTR(kode, LOCATE('/', kode, 8) + 1)) AS counter FROM thorderjual WHERE kode LIKE '%$prefix%'";
        $query = "SELECT MAX(SUBSTR(kode, LOCATE('/', kode, LOCATE('/', kode) + 1) + 1)) AS counter FROM thorderjual WHERE kode LIKE '%SOP%'";
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
    function getNomorHeader($_headerkode){
        $result = $this->db->query("SELECT akhir counter FROM tcount WHERE kode = '$_headerkode'")->row()->counter;
        return $result + 1;
    }

    //added by Tonny
    //untuk mendapatkan nomor baru untuk salesorder detail
    function getNomorDetail($_detailkode){
        $result = $this->db->query("SELECT akhir counter FROM tcount WHERE kode = '$_detailkode'")->row()->counter;
        return $result + 1;
    }

    //untuk testing saja
    function getHeader_get(){
        $this->response($this->getNomorHeader('ttransaksi'));
    }
//    function getCounterDetail_post(){
//        $data['data'] = array();
//
//        $value = file_get_contents('php://input');
//        $jsonObject = (json_decode($value , true));
//        $kode = (isset($jsonObject["kode"]) ? $this->clean($jsonObject["kode"])     : "tdorderjual");  //input merupakan kodetransaksi
//        $query = "SELECT akhir counter FROM tcount WHERE kode = '$kode'";
//        $result = $this->db->query($query);
//        if( $result && $result->num_rows() > 0){
//            foreach ($result->result_array() as $r){
//                array_push($data['data'], array(
//                                                'counter'	=> $r['counter']
//                                                )
//                );
//            }
//        }else{
//            array_push($data['data'], array( 'query' => $this->error($query) ));
//        }
//
//        if ($data){
//            // Set the response and exit
//            $this->response($data['data']); // OK (200) being the HTTP response code
//        }
//    }
}
