from pathlib import Path
import re
import unittest


PACKAGE_DIR = Path(__file__).resolve().parents[1] / "legacy" / "oracle" / "packages"


class WeightLimitSingleSourceTest(unittest.TestCase):
    def test_air_weight_limit_is_owned_by_service_eligibility(self):
        eligibility_pkg = (PACKAGE_DIR / "SERVICE_ELIGIBILITY_PKG.sql").read_text()
        rating_pkg = (PACKAGE_DIR / "SHIPMENT_RATING_PKG.sql").read_text()

        self.assertIn("c_max_air_weight CONSTANT NUMBER := 150", eligibility_pkg)
        self.assertRegex(
            eligibility_pkg,
            re.compile(
                r"p_weight_lbs\s*>\s*c_max_air_weight\s+"
                r"AND\s+p_service_code\s+IN\s+\('EXP',\s*'PO'\)",
                re.IGNORECASE,
            ),
        )
        self.assertNotRegex(
            rating_pkg,
            re.compile(
                r"p_weight_lbs\s*>\s*\d+\s+AND\s+p_service_code\s*=\s*'PO'",
                re.IGNORECASE,
            ),
        )


if __name__ == "__main__":
    unittest.main()
