package app;

import java.util.Arrays;

public class Algorithm implements Comparable{
    private int parentId = 0;
    private int childId = 0;
    private boolean isShort = false;
    private int pairPointId = 0;
    private int[] occupy = new int[7];
    private String[] conditions = new String[7];
    private int nextPointId = 0;

    public Algorithm(int parentId, int childId, boolean isShort, int pairPointId, int[] occupy, String[] conditions, int nextPointId) {
        this.parentId = parentId;
        this.childId = childId;
        this.isShort = isShort;
        this.pairPointId = pairPointId;
        if (occupy.length == 7) {
            this.occupy = occupy.clone();
        }
        if (conditions.length == 7) {
            this.conditions = conditions.clone();
        }
        this.nextPointId = nextPointId;
    }

    public boolean isApply(boolean isShort, int[] occupy, int pairPointId) {
        if (this.isShort == isShort && this.pairPointId == pairPointId) {
            for (int i=0; i<this.occupy.length; i++) {
                switch (conditions[i]) {
                    case "le":
                        if (occupy[i] > this.occupy[i]) return false;
                        break;
                    case "me":
                        if (occupy[i] < this.occupy[i]) return false;
                        break;
                    case "eq":
                        if (occupy[i] != this.occupy[i]) return false;
                        break;
                    case "pass":
                    default:
                        break;
                }
            }
            return true;
        }
        return false;
    }

    public int getParentId() {
        return parentId;
    }

    public int getChildId() {
        return childId;
    }

    public boolean getIsShort() {
        return isShort;
    }

    public int getPairPointId() {
        return pairPointId;
    }

    public int getNextPointId() {
        return nextPointId;
    }

    public int[] getOccupy() {
        return occupy;
    }

    public String[] getConditions() {
        return conditions;
    }

    public String toString() {
        String result = "Rule" + parentId + "-" + childId + " :: " + (isShort?"Short":"Long") + ", " + "nextPoint: " + nextPointId;
        return result;
    }

    public String showRule() {
        String result = "Rule" + parentId + "-" + childId + " :: " + (isShort?"r_i.s = Short ∧ r_i.id = High":"r_i.s = Long ∧ r_i.pair = " + pairPointId) + " ∧ \n";
        for (int i=0; i<7; i++) {
            switch (conditions[i]) {
                case "le":
                    result += "Occupy(r_i." + i + ") <= " + occupy[i] + (i!=6?" ∧ ":"");
                    break;
                case "me":
                result += "Occupy(r_i." + i + ") >= " + occupy[i] + (i!=6?" ∧ ":"");
                    break;
                case "pass":
                    break;
                case "eq":
                default:
                result += "Occupy(r_i." + i + ") = " + occupy[i] + (i!=6?" ∧ ":"");
                    break;
            }
            if (i % 2 == 1) {
                result += "\n";
            }
        }
        result += " -> r_iはr_i." + nextPointId + "に移動";
        return result;
    }

    public String generateJSONFormat() {
        String resultString =  "\t{\n" + 
        "\t\t\"parentId\": " + this.parentId + ",\n" + 
        "\t\t\"childId\": " + this.childId + ",\n" + 
        "\t\t\"isShort\": " + this.isShort + ",\n" + 
        "\t\t\"pairPointId\": " + this.pairPointId + ",\n" + 
        "\t\t\"occupy\": " + Arrays.toString(this.occupy) + ",\n" + 
        "\t\t\"conditions\": [";
        for (int i=0; i<conditions.length; i++) {
            resultString += "\"" + conditions[i] + "\"" + (i < conditions.length - 1?", ":"");
        }
        resultString += "],\n" + 
        "\t\t\"nextPointId\": " + this.nextPointId + "\n" + 
        "\t}";

        return resultString;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Algorithm)) {
            return 0;
        }
        Algorithm compareRule = (Algorithm)o;
        int compareNum = compareRule.getParentId() * 10000 + compareRule.getChildId();
        int thisNum = this.parentId * 10000 + this.childId;
        if (compareNum < thisNum) {
            return 1;
        }
        else if (compareNum > thisNum) {
            return -1;
        }
        return 0;
    }
}
