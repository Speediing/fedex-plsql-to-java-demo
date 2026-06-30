SET SERVEROUTPUT ON

VAR total NUMBER;
VAR err_code NUMBER;
VAR err_msg VARCHAR2(200);

BEGIN
    SHIPMENT_RATING_PKG.rate_shipment(
        p_tracking_ref  => 'DEMO-001',
        p_origin_zip    => '38118',
        p_dest_zip      => '75201',
        p_dest_type     => 'R',
        p_dest_region   => 'US',
        p_weight_lbs    => 25,
        p_length_in     => 12,
        p_width_in      => 10,
        p_height_in     => 8,
        p_service_code  => 'GND',
        p_account_id    => 'ACCT-1001',
        p_saturday_flag => 'N',
        p_dg_flag       => 'N',
        p_total_amount  => :total,
        p_error_code    => :err_code,
        p_error_msg     => :err_msg
    );
END;
/

PRINT total;
PRINT err_code;
PRINT err_msg;
