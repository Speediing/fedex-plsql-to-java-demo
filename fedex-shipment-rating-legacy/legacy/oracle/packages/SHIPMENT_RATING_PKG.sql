CREATE OR REPLACE PACKAGE SHIPMENT_RATING_PKG AS
    /*
     * Monolithic rating entry point. Calculates, persists audit row, and returns totals.
     * Business rules are split across cursors, local variables, and duplicated checks.
     */
    g_last_zone NUMBER;  -- shared package state (legacy smell)

    PROCEDURE rate_shipment (
        p_tracking_ref    IN  VARCHAR2,
        p_origin_zip      IN  VARCHAR2,
        p_dest_zip        IN  VARCHAR2,
        p_dest_type       IN  CHAR,      -- R=residential, C=commercial
        p_dest_region     IN  VARCHAR2,  -- US or non-US
        p_weight_lbs      IN  NUMBER,
        p_length_in       IN  NUMBER,
        p_width_in        IN  NUMBER,
        p_height_in       IN  NUMBER,
        p_service_code    IN  VARCHAR2,
        p_account_id      IN  VARCHAR2,
        p_saturday_flag   IN  CHAR,
        p_dg_flag         IN  CHAR,
        p_total_amount    OUT NUMBER,
        p_error_code      OUT NUMBER,
        p_error_msg       OUT VARCHAR2
    );
END SHIPMENT_RATING_PKG;
/

CREATE OR REPLACE PACKAGE BODY SHIPMENT_RATING_PKG AS

    c_fuel_factor       CONSTANT NUMBER := 0.12;
    c_res_gnd_surcharge CONSTANT NUMBER := 4.95;
    c_res_air_surcharge CONSTANT NUMBER := 5.95;
    c_sat_surcharge     CONSTANT NUMBER := 16.00;
    c_oversize_fee      CONSTANT NUMBER := 85.00;
    c_dg_surcharge      CONSTANT NUMBER := 35.00;
    c_heavy_threshold   CONSTANT NUMBER := 70;
    c_heavy_per_lb      CONSTANT NUMBER := 0.50;

    FUNCTION is_oversize (
        p_length IN NUMBER,
        p_width  IN NUMBER,
        p_height IN NUMBER
    ) RETURN CHAR IS
        v_girth NUMBER;
    BEGIN
        v_girth := p_length + 2 * (p_width + p_height);
        IF v_girth > 165 THEN
            RETURN 'Y';
        END IF;
        RETURN 'N';
    END is_oversize;

    FUNCTION tier_discount_pct (p_account_id VARCHAR2) RETURN NUMBER IS
        v_tier account_tier.tier_code%TYPE;
    BEGIN
        SELECT tier_code INTO v_tier
          FROM account_tier
         WHERE account_id = p_account_id;

        IF v_tier = 'GOLD' THEN
            RETURN 0.10;
        ELSIF v_tier = 'SILVER' THEN
            RETURN 0.05;
        END IF;
        RETURN 0;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
    END tier_discount_pct;

    PROCEDURE rate_shipment (
        p_tracking_ref    IN  VARCHAR2,
        p_origin_zip      IN  VARCHAR2,
        p_dest_zip        IN  VARCHAR2,
        p_dest_type       IN  CHAR,
        p_dest_region     IN  VARCHAR2,
        p_weight_lbs      IN  NUMBER,
        p_length_in       IN  NUMBER,
        p_width_in        IN  NUMBER,
        p_height_in       IN  NUMBER,
        p_service_code    IN  VARCHAR2,
        p_account_id      IN  VARCHAR2,
        p_saturday_flag   IN  CHAR,
        p_dg_flag         IN  CHAR,
        p_total_amount    OUT NUMBER,
        p_error_code      OUT NUMBER,
        p_error_msg       OUT VARCHAR2
    ) IS
        v_origin_prefix VARCHAR2(3);
        v_dest_prefix   VARCHAR2(3);
        v_zone          NUMBER;
        v_base          NUMBER;
        v_subtotal      NUMBER := 0;
        v_oversize      CHAR(1);
        v_discount_pct  NUMBER;
    BEGIN
        p_total_amount := 0;
        p_error_code   := 0;
        p_error_msg    := NULL;

        v_origin_prefix := SUBSTR(p_origin_zip, 1, 3);
        IF p_dest_region = 'US' THEN
            v_dest_prefix := SUBSTR(p_dest_zip, 1, 3);
        ELSE
            v_dest_prefix := '000';
        END IF;

        BEGIN
            SELECT zone_code INTO v_zone
              FROM zone_matrix
             WHERE origin_prefix = v_origin_prefix
               AND dest_prefix = v_dest_prefix;
            g_last_zone := v_zone;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                p_error_code := -2001;
                p_error_msg  := 'Unknown lane';
                RETURN;
        END;

        v_oversize := is_oversize(p_length_in, p_width_in, p_height_in);

        SERVICE_ELIGIBILITY_PKG.check_service(
            p_service_code, p_weight_lbs, p_dg_flag, p_dest_region, v_oversize,
            p_error_code, p_error_msg
        );
        IF p_error_code <> 0 THEN
            RETURN;
        END IF;

        -- duplicated weight guard (legacy drift)
        IF p_weight_lbs > 150 AND p_service_code = 'PO' THEN
            p_error_code := -2041;
            p_error_msg  := 'Weight exceeds air service limit';
            RETURN;
        END IF;

        BEGIN
            SELECT base_amount INTO v_base
              FROM service_base_rate
             WHERE zone_code = v_zone
               AND service_code = p_service_code;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                p_error_code := -2002;
                p_error_msg  := 'No rate for service/zone';
                RETURN;
        END;

        v_subtotal := v_base;

        IF p_weight_lbs > c_heavy_threshold AND p_service_code = 'GND' THEN
            v_subtotal := v_subtotal + (p_weight_lbs - c_heavy_threshold) * c_heavy_per_lb;
        END IF;

        IF p_dest_type = 'R' THEN
            IF p_service_code = 'GND' THEN
                v_subtotal := v_subtotal + c_res_gnd_surcharge;
            ELSE
                v_subtotal := v_subtotal + c_res_air_surcharge;
            END IF;
        END IF;

        IF p_saturday_flag = 'Y' AND p_service_code IN ('EXP', 'PO') THEN
            v_subtotal := v_subtotal + c_sat_surcharge;
        END IF;

        IF v_oversize = 'Y' AND p_service_code = 'GND' THEN
            v_subtotal := v_subtotal + c_oversize_fee;
        END IF;

        IF p_dg_flag = 'Y' AND p_service_code = 'EXP' THEN
            v_subtotal := v_subtotal + c_dg_surcharge;
        END IF;

        v_subtotal := ROUND(v_subtotal * (1 + c_fuel_factor), 2);

        v_discount_pct := tier_discount_pct(p_account_id);
        p_total_amount := ROUND(v_subtotal * (1 - v_discount_pct), 2);

        INSERT INTO rating_audit (tracking_ref, service_code, total_amount, error_code)
        VALUES (p_tracking_ref, p_service_code, p_total_amount, 0);

    END rate_shipment;

END SHIPMENT_RATING_PKG;
/
