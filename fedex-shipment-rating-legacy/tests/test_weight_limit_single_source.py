import re
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RATING_PACKAGE = ROOT / "legacy/oracle/packages/SHIPMENT_RATING_PKG.sql"
ELIGIBILITY_PACKAGE = ROOT / "legacy/oracle/packages/SERVICE_ELIGIBILITY_PKG.sql"


def _normalize_sql(sql: str) -> str:
    return re.sub(r"\s+", " ", sql.upper())


class WeightLimitSingleSourceTest(unittest.TestCase):
    def test_eligibility_package_owns_air_weight_limit(self) -> None:
        eligibility_sql = _normalize_sql(ELIGIBILITY_PACKAGE.read_text())

        self.assertIn("C_MAX_AIR_WEIGHT CONSTANT NUMBER := 150", eligibility_sql)
        self.assertRegex(
            eligibility_sql,
            r"IF P_WEIGHT_LBS > C_MAX_AIR_WEIGHT AND P_SERVICE_CODE IN \('EXP', 'PO'\)",
        )
        self.assertIn("P_ERROR_CODE := -2041", eligibility_sql)

    def test_rate_shipment_does_not_duplicate_air_weight_limit(self) -> None:
        rating_sql = _normalize_sql(RATING_PACKAGE.read_text())

        self.assertIn("SERVICE_ELIGIBILITY_PKG.CHECK_SERVICE(", rating_sql)
        self.assertNotRegex(
            rating_sql,
            r"IF P_WEIGHT_LBS > 150 AND P_SERVICE_CODE = 'PO' THEN",
            "rate_shipment must not reimplement the air weight guard after eligibility",
        )


if __name__ == "__main__":
    unittest.main()
