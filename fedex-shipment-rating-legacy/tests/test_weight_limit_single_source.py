import re
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]
LEGACY_PACKAGES = REPO_ROOT / "fedex-shipment-rating-legacy" / "legacy" / "oracle" / "packages"


class WeightLimitSingleSourceTest(unittest.TestCase):
    def test_air_weight_limit_is_defined_in_service_eligibility(self):
        eligibility_package = (LEGACY_PACKAGES / "SERVICE_ELIGIBILITY_PKG.sql").read_text()

        self.assertRegex(
            eligibility_package,
            r"c_max_air_weight\s+CONSTANT\s+NUMBER\s*:=\s*150",
        )
        self.assertRegex(
            eligibility_package,
            r"p_weight_lbs\s*>\s*c_max_air_weight\s+AND\s+p_service_code\s+IN\s*\('EXP',\s*'PO'\)",
        )

    def test_rating_package_does_not_duplicate_air_weight_limit_after_eligibility(self):
        rating_package = (LEGACY_PACKAGES / "SHIPMENT_RATING_PKG.sql").read_text()
        post_eligibility_block = rating_package.split("SERVICE_ELIGIBILITY_PKG.check_service", maxsplit=1)[1]

        duplicate_guard = re.search(
            r"IF\s+p_weight_lbs\s*>\s*150\s+AND\s+p_service_code\s*=\s*'PO'\s+THEN",
            post_eligibility_block,
            flags=re.IGNORECASE,
        )

        self.assertIsNone(
            duplicate_guard,
            "rate_shipment must rely on SERVICE_ELIGIBILITY_PKG for air weight limits",
        )


if __name__ == "__main__":
    unittest.main()
