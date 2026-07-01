from pathlib import Path
import re
import unittest


ROOT = Path(__file__).resolve().parents[1]
RATING_PACKAGE = ROOT / "legacy" / "oracle" / "packages" / "SHIPMENT_RATING_PKG.sql"
ELIGIBILITY_PACKAGE = ROOT / "legacy" / "oracle" / "packages" / "SERVICE_ELIGIBILITY_PKG.sql"


class WeightLimitSingleSourceTest(unittest.TestCase):
    def test_air_weight_limit_is_owned_by_service_eligibility(self) -> None:
        eligibility_sql = ELIGIBILITY_PACKAGE.read_text(encoding="utf-8")

        self.assertRegex(
            eligibility_sql,
            r"c_max_air_weight\s+CONSTANT\s+NUMBER\s*:=\s*150",
        )
        self.assertRegex(
            eligibility_sql,
            r"p_weight_lbs\s*>\s*c_max_air_weight\s+AND\s+p_service_code\s+IN\s*\(\s*'EXP'\s*,\s*'PO'\s*\)",
        )

    def test_rate_shipment_does_not_repeat_priority_overnight_weight_guard(self) -> None:
        rating_sql = RATING_PACKAGE.read_text(encoding="utf-8")
        after_eligibility_check = rating_sql.split("SERVICE_ELIGIBILITY_PKG.check_service", maxsplit=1)[1]

        duplicate_guard = re.search(
            r"p_weight_lbs\s*>\s*\d+\s+AND\s+p_service_code\s*=\s*'PO'",
            after_eligibility_check,
        )

        self.assertIsNone(
            duplicate_guard,
            "rate_shipment must rely on SERVICE_ELIGIBILITY_PKG.check_service for air weight limits",
        )


if __name__ == "__main__":
    unittest.main()
