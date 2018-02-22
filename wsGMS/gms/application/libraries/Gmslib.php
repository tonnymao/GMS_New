<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Gmslib {

	/**
	 * saat user buka form, 
	 * Kode: field nilai akhir tidak +1 (tidak diupdate nilainya table tcount), tapi secara tampilan di form +1 manual
	 * nomor: field nilai akhir +1 (update nilai akhir table tcount), berlaku untuk header(TTRANSAKSI) & detail (TDORDERBELI)
	 */
	
	private $CI;
	private $debug;

	public function __construct()
	{
		$this->CI =& get_instance();
		$this->debug = 1;
	}

	// GMS
	// menampilkan nilai akhir berdasarkan $kode di field kode
	public function get_last_tcount($kode)
	{
		$result = array();
		$nomor_akhir = NULL;
		$query_debug = NULL;

		$rowdata = $this->CI->db->query('
			SELECT 
			  *
			FROM tcount a 
			WHERE a.kode = "'.$kode.'"
		')->row_array();

		if( count($rowdata) > 0 ) {
			$nomor_akhir = $rowdata['akhir'];
		}

		if( $this->debug == 1 ) {
			$query_debug = $this->CI->db->last_query();
		}

		$result = array(
			'akhir' => $nomor_akhir,
			'debug' => str_replace(array("\n", "\r", "\t"),'' , $query_debug)
		);

		return $result;
	}

	// HANYA UPDATE NILAI `akhir` ditable tcount
	protected function update_nomor_tcount($kode)
	{
		$nomor_akhir = $this->get_last_tcount($kode);
		
		$data_update = array(
			'akhir' => ($nomor_akhir['akhir']+1),
		);

		$this->CI->db->where('kode', $kode);
		$this->CI->db->update('tcount', $data_update);

		return $data_update;
	}

	public function generate_nomor($kode)
	{		
		/**
		 * STEP generate_nomor:
		 * 1. panggil update_nomor_tcount($kode)
		 * 2. panggil get_last_tcount($kode)
		 */
		$this->update_nomor_tcount($kode);

		return $this->get_last_tcount($kode);
	}

	public function get_formatsetting($kode)
	{
		$formatsetting = NULL;
		$query_debug = NULL;
		$result = array();

		$rowdata = $this->CI->db->query('
			SELECT 
			  *
			FROM tformatsetting a 
			WHERE a.kode = "'.$kode.'"
		')->row_array();

		if( count($rowdata) > 0 ) {
			$formatsetting = $rowdata['formatsetting'];
		}

		if( $this->debug == 1 ) {
			$query_debug = $this->CI->db->last_query();
		}

		$result = array(
			'formatsetting' => $formatsetting,
			'debug' => str_replace(array("\n", "\r", "\t"),'' , $query_debug)
		);

		return $result;
	}

	/**
	 * proses generate kode hanya +1 secara tampilan, tapi tidak update nilai field `akhir` ditable tcount
	 * @param  [type] $formatsetting [description]
	 * @param  string $kodecabang    [description]
	 * @param  string $mode (view|insert|update) 		 sebagai penentu apakah update nilai field 'akhir' tcount, jika mode = view, +1 hanya tampilan, jika mode insert update tcount (nilai akhir +1)
	 * @return string                $result['kode'] => yang nanti formatnya = OB-01/1711, numerator countnya nnti dari tcount
	 */
	public function generate_kode($formatsetting, $kodecabang = '01')
	{
		$result = array();
		$kode = NULL;
		$query_debug = NULL;
		$date_month = 'm';
		$date_year = 'Y';
		$date_day = 'd';

		$separator = $this->get_formatsetting('SEPARATOR');
		$piece_formatsetting = explode(',', $formatsetting);
		$prefix = $piece_formatsetting[0];
		$max_digit = $piece_formatsetting[1];
		$date_format = $piece_formatsetting[2];
		$theader = $piece_formatsetting[3];
		$tdetail = isset($piece_formatsetting[4]) ? $piece_formatsetting[4] : NULL;
		$string_month = substr($date_format, strpos($date_format, 'M'));
		$string_year = str_replace($string_month, '', $date_format);

		if( strlen($string_year) == 2 ) {
			$date_year = 'y';
		}
		elseif( strlen($string_year) == 4 ) {
			$date_year = 'Y';
		}

		$formatted_date = date($date_year.$date_month);
		$rawkode = $prefix.'-'.$kodecabang.$separator['formatsetting'].$formatted_date.$separator['formatsetting'];

		$nomor_akhir = $this->get_last_tcount($rawkode);
		$numerator = str_pad(($nomor_akhir['akhir']+1), $max_digit, '0', STR_PAD_LEFT);
		$kode = $rawkode.$numerator;

		if( $this->debug == 1 ) {
			$query_debug = $this->CI->db->last_query();
		}

		$result = array(
			'kode' => $kode,
			'raw_kode' => $rawkode,
			'debug' => str_replace(array("\n", "\r", "\t"),'' , $query_debug)
		);

		return $result;		
	}
}