package com.fedexdemo.rating.reference;

import com.fedexdemo.rating.domain.AccountTier;
import com.fedexdemo.rating.domain.Money;
import com.fedexdemo.rating.domain.ServiceLevel;
import java.util.Map;
import java.util.Optional;

public final class ReferenceData {

    private static final Map<LaneKey, Integer> ZONES = Map.of(
            new LaneKey("381", "752"), 4,
            new LaneKey("381", "100"), 5,
            new LaneKey("381", "902"), 6,
            new LaneKey("381", "000"), 8);

    private static final Map<RateKey, Money> BASE_RATES = Map.ofEntries(
            Map.entry(new RateKey(4, ServiceLevel.GROUND), Money.of(18.50)),
            Map.entry(new RateKey(4, ServiceLevel.EXPRESS), Money.of(42.00)),
            Map.entry(new RateKey(4, ServiceLevel.PRIORITY_OVERNIGHT), Money.of(58.00)),
            Map.entry(new RateKey(5, ServiceLevel.GROUND), Money.of(22.00)),
            Map.entry(new RateKey(5, ServiceLevel.EXPRESS), Money.of(48.00)),
            Map.entry(new RateKey(5, ServiceLevel.PRIORITY_OVERNIGHT), Money.of(65.00)),
            Map.entry(new RateKey(6, ServiceLevel.GROUND), Money.of(26.00)),
            Map.entry(new RateKey(6, ServiceLevel.EXPRESS), Money.of(55.00)),
            Map.entry(new RateKey(6, ServiceLevel.PRIORITY_OVERNIGHT), Money.of(72.00)),
            Map.entry(new RateKey(8, ServiceLevel.EXPRESS), Money.of(95.00)));

    private static final Map<String, AccountTier> ACCOUNT_TIERS = Map.of(
            "ACCT-1001", AccountTier.STANDARD,
            "ACCT-2002", AccountTier.SILVER,
            "ACCT-3003", AccountTier.GOLD);

    private ReferenceData() {
    }

    public static Optional<Integer> findZone(String originPrefix, String destPrefix) {
        return Optional.ofNullable(ZONES.get(new LaneKey(originPrefix, destPrefix)));
    }

    public static Optional<Money> findBaseRate(int zone, ServiceLevel service) {
        return Optional.ofNullable(BASE_RATES.get(new RateKey(zone, service)));
    }

    public static AccountTier accountTierFor(String accountId) {
        return ACCOUNT_TIERS.getOrDefault(accountId, AccountTier.STANDARD);
    }

    private record LaneKey(String originPrefix, String destPrefix) {
    }

    private record RateKey(int zone, ServiceLevel service) {
    }
}
