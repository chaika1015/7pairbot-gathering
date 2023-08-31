package app;

import java.util.Collections;
import java.util.List;

public class RuleManagement {
    private List<Algorithm> rules;
    private RuleManageListener listener = null;
    private TabManageListener listener_tab = null;
    private int selectedIndex = -1;
    private boolean edited = false;
    
    public RuleManagement() {
        rules = readJson.vanillaDecodeJSON();
        // rules = readJson.decodeJSON();
    }

    public List<Algorithm> getRules() {
        return rules;
    }

    public int getRulesSize() {
        return rules.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public boolean isEdited() {
        return edited;
    }

    public int getApplyIndex(int parentId, int childId) {
        int result = -1;
        for (int i=0; i<rules.size(); i++) {
            if (rules.get(i).getParentId() == parentId && rules.get(i).getChildId() == childId) {
                result = i;
                break;
            }
        }
        return result;
    }

    public void AddManageListener (RuleManageListener listener) {
        this.listener = listener;
    }
    public void removeManageListener () {
        this.listener = null;
    }
    public void AddTabManageListener (TabManageListener listener) {
        this.listener_tab = listener;
    }
    public void removeTabManageListener () {
        this.listener_tab = null;
    }

    public void selectRuleByLogger(int selectedIndex) {
        if (listener != null) {
            this.selectedIndex = selectedIndex;
            listener.ruleSelectedByLogger(new RuleManageEvent(this));
            listener_tab.selectRuleTab(new TabManageEvent(this));
        }
    }

    public void addRule(Algorithm rule) {
        if (listener != null) {
            rules.add(rule);
            Collections.sort(rules);
            edited = true;
            listener.ruleChanged(new RuleManageEvent(this));
        }
    }

    public void updateRule(int selectedIndex, Algorithm rule) {
        if (listener != null) {
            rules.remove(selectedIndex);
            rules.add(rule);
            Collections.sort(rules);
            edited = true;
            listener.ruleChanged(new RuleManageEvent(this));
        }
    }

    public void deleteRule(int selectedIndex) {
        if (listener != null) {
            rules.remove(selectedIndex);
            edited = true;
            listener.ruleChanged(new RuleManageEvent(this));
        }
    }

    public void saveRule() {
        readJson.writeRules(rules.toArray(new Algorithm[rules.size()]));
    }
}
