
export function formatOdds(decimalOdds) {
    const oddsFormat = localStorage.getItem('oddsFormat') || 'DECIMAL';

    if (!decimalOdds) return '-';

    if (oddsFormat === 'AMERICAN') {
        if (decimalOdds >= 2) {
            return `+${Math.round((decimalOdds - 1) * 100)}`;
        }

        return `${Math.round(-100 / (decimalOdds - 1))}`;
    }

    return Number(decimalOdds).toFixed(2);
}