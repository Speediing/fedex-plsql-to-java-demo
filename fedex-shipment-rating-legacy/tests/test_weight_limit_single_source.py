from pathlib import Path
import re
import unittest


ROOT = Path(__file__).resolve().parents[1]
PACKAGES = ROOT / "legacy" / "oracle" / "packages"


class WeightLimitSingleSourceTest(unittest.TestCase):
    def setUp(self):
        self.eligibility_sql = (PACKAGES / "SERVICE_ELIGIBILITY_PKG.sql").read_text()
        self.rating_sql = (PACKAGES / "SHIPMENT_RATING_PKG.sql").read_text()

    def test_air_weight_limit_is_owned_by_service_eligibility(self):
        self.assertRegex(
            self.eligibility_sql,
            r"c_max_air_weight\s+CONSTANT\s+NUMBER\s*:=\s*150",
            "SERVICE_ELIGIBILITY_PKG should expose the single air-weight threshold constant",
        )
        self.assertRegex(
            self.eligibility_sql,
            r"p_weight_lbs\s*>\s*c_max_air_weight\s+AND\s+p_service_code\s+IN\s*\('EXP',\s*'PO'\)",
            "SERVICE_ELIGIBILITY_PKG should apply the threshold to both air services",
        )

    def test_rate_shipment_does_not_repeat_air_weight_limit(self):
        self.assertIn(
            "SERVICE_ELIGIBILITY_PKG.check_service",
            self.rating_sql,
            "rate_shipment should delegate eligibility decisions before pricing",
        )
        self.assertNotRegex(
            self.rating_sql,
            re.compile(r"p_weight_lbs\s*>\s*150\s+AND\s+p_service_code\s*=\s*'PO'", re.IGNORECASE),
            "rate_shipment must not repeat the air-weight rule with a hard-coded PO-only guard",
        )


if __name__ == "__main__":
    unittest.main()
