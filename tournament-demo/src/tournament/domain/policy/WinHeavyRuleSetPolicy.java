package tournament.domain.policy;

public class WinHeavyRuleSetPolicy implements RuleSetPolicy {
    public int getPointsForWin() { return 5; }
    public int getPointsForDraw() { return 0; }
}
