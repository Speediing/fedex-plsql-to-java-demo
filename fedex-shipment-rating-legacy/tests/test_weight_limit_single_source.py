import re
import unittest
from pathlib import Path


PACKAGE_DIR = Path(__file__).resolve().parents[1] / "legacy" / "oracle" / "packages"
RATING_PACKAGE = PACKAGE_DIR / "SHIPMENT_RATING_PKG.sql"
ELIGIBILITY_PACKAGE = PACKAGE_DIR / "SERVICE_ELIGIBILITY_PKG.sql"


def collapse_sql(sql: str) -> str:
    return re.sub(r"\s+", " ", sql)


class WeightLimitSingleSourceTest(unittest.TestCase):
    def test_service_eligibility_owns_air_weight_limit(self) -> None:
        eligibility_sql = collapse_sql(ELIGIBILITY_PACKAGE.read_text(encoding="utf-8"))

        self.assertIn("c_max_air_weight CONSTANT NUMBER := 150", eligibility_sql)
        self.assertRegex(
            eligibility_sql,
            r"IF p_weight_lbs > c_max_air_weight AND p_service_code IN \('EXP', 'PO'\) THEN",
        )
        self.assertIn("p_error_code := -2041", eligibility_sql)

    def test_rate_shipment_does_not_recheck_air_weight_after_eligibility(self) -> None:
        rating_sql = collapse_sql(RATING_PACKAGE.read_text(encoding="utf-8"))
        _, post_eligibility_sql = rating_sql.split("SERVICE_ELIGIBILITY_PKG.check_service", 1)

        duplicate_guard = re.search(
            r"IF p_weight_lbs > \d+ AND p_service_code = 'PO' THEN "
            r"p_error_code := -2041; "
            r"p_error_msg := 'Weight exceeds air service limit'; "
            r"RETURN; "
            r"END IF;",
            post_eligibility_sql,
        )

        self.assertIsNone(
            duplicate_guard,
            "rate_shipment must not duplicate the air weight guard after check_service; "
            "SERVICE_ELIGIBILITY_PKG.check_service is the single source of truth.",
        )


if __name__ == "__main__":
    unittest.main()
