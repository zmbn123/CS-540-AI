
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
 *
 * You must add code for the 1 member and 4 methods specified below.
 *
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree
{

    private DecTreeNode root;
    //ordered list of class labels
    private List<String> labels;
    //ordered list of attributes
    private List<String> attributes;
    //map to ordered discrete values taken by attributes
    private Map<String, List<String>> attributeValues;

    /**
     * Answers static questions about decision trees.
     */
    DecisionTreeImpl()
    {
        // no code necessary this is void purposefully
    }

    /**
     * Build a decision tree given only a training set.
     *
     * @param train: the training set
     */
    DecisionTreeImpl(DataSet train)
    {

        this.labels = train.labels;
        this.attributes = train.attributes;
        this.attributeValues = train.attributeValues;
        // TODO: add code here
        buildTree(null, train.instances, new ArrayList<String>(attributes), "", getMajority(train.instances));
    }

    /**
     * Build a decision tree given a training set then prune it using a tuning
     * set.
     *
     * @param train: the training set
     * @param tune: the tuning set
     */
    DecisionTreeImpl(DataSet train, DataSet tune)
    {

        this.labels = train.labels;
        this.attributes = train.attributes;
        this.attributeValues = train.attributeValues;
        buildTree(null, train.instances, new ArrayList<String>(attributes), "", getMajority(train.instances));
        for (int i = 0; i < 100; ++i)
        {
            double lastAccuracy = calculateAccuracy(tune);
            DecTreeNode nodeCandiate = null;
            List<DecTreeNode> nonTerminalNodes = nonTerminalBFS(root);
            double candidateAccuracy = -1;
            for (DecTreeNode currNode : nonTerminalNodes)
            {
                currNode.terminal = true;
                double accuracy = calculateAccuracy(tune);
                currNode.terminal = false;
                if (accuracy > candidateAccuracy)
                {
                    candidateAccuracy = accuracy;
                    nodeCandiate = currNode;
                }
            }
            if (candidateAccuracy >= lastAccuracy)
            {
                nodeCandiate.children = null;
                nodeCandiate.terminal = true;
            }
        }
    }

    private List<DecTreeNode> nonTerminalBFS(DecTreeNode root)
    {
        List<DecTreeNode> start = new ArrayList<DecTreeNode>();
        List<DecTreeNode> done = new ArrayList<DecTreeNode>();
        List<DecTreeNode> justAdded, edge;

        for (DecTreeNode child : root.children)
        {
            if (!child.terminal)
            {
                start.add(child);
            }
        }

        justAdded = expand(start);
        done.addAll(start);

        do
        {
            edge = expand(justAdded);
            done.addAll(justAdded);
            justAdded = expand(edge);
            if (justAdded.isEmpty())
            {
                done.addAll(edge);
                break;
            }
        } while (justAdded.size() != edge.size());

        return done;
    }

    private List<DecTreeNode> expand(List<DecTreeNode> basket)
    {
        List<DecTreeNode> toAdd = new ArrayList<DecTreeNode>();
        for (DecTreeNode node : basket)
        {
            if (!node.terminal)
            {
                for (DecTreeNode child : node.children)
                {
                    if (!child.terminal)
                    {
                        toAdd.add(child);
                    }
                }
            }
        }
        return toAdd;
    }

    @Override
    public String classify(Instance instance)
    {
        DecTreeNode currNode = root;
        while (true)
        {
            if (currNode.terminal)
            {
                break;
            } else
            {
                String currAttr = currNode.attribute;
                String instanceValue = instance.attributes.
                        get(getAttributeIndex(currAttr));
                DecTreeNode foundNode = null;
                for (DecTreeNode node : currNode.children)
                {
                    if (node.parentAttributeValue.
                            equals(instanceValue))
                    {
                        foundNode = node;
                        break;
                    }
                }
                assert (foundNode != null);
                currNode = foundNode;
            }
        }
        return currNode.label;
    }

    @Override
    /**
     * Print the decision tree in the specified format
     */
    public void print()
    {

        printTreeNode(root, null, 0);
    }

    /**
     * Prints the subtree of the node with each line prefixed by 4 * k spaces.
     */
    public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < k; i++)
        {
            sb.append("    ");
        }
        String value;
        if (parent == null)
        {
            value = "ROOT";
        } else
        {
            int attributeValueIndex = this.
                    getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
            value = attributeValues.get(parent.attribute).
                    get(attributeValueIndex);
        }
        sb.append(value);
        if (p.terminal)
        {
            sb.append(" (" + p.label + ")");
            System.out.println(sb.toString());
        } else
        {
            sb.append(" {" + p.attribute + "?}");
            System.out.println(sb.toString());
            for (DecTreeNode child : p.children)
            {
                printTreeNode(child, p, k + 1);
            }
        }
    }

    /**
     * Helper function to get the index of the label in labels list
     */
    private int getLabelIndex(String label)
    {
        for (int i = 0; i < this.labels.size(); i++)
        {
            if (label.equals(this.labels.get(i)))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper function to get the index of the attribute in attributes list
     */
    private int getAttributeIndex(String attr)
    {
        for (int i = 0; i < this.attributes.size(); i++)
        {
            if (attr.equals(this.attributes.get(i)))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper function to get the index of the attributeValue in the list for
     * the attribute key in the attributeValues map
     */
    private int getAttributeValueIndex(String attr, String value)
    {
        for (int i = 0; i < attributeValues.get(attr).size(); i++)
        {
            if (value.equals(attributeValues.get(attr).get(i)))
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void rootInfoGain(DataSet train)
    {
        this.labels = train.labels;
        this.attributes = train.attributes;
        this.attributeValues = train.attributeValues;
        double entropy = calculateEntropy(train.instances);
        double mutualInformation = 0.0;
        for (int i = 0; i < train.attributes.size(); i++)
        { //For each attribute
            double conditionalEntropy = calculateConditionalEntropy(attributes.
                    get(i), train.instances);
            mutualInformation = entropy - conditionalEntropy;
            System.out.
                    printf("%s %.5f\n", train.attributes.get(i) + " ", mutualInformation);
        }
    }

    private double calculateEntropy(List<Instance> instances)
    {
        int[] labelCounts = new int[labels.size()];
        int totalInstances = instances.size();
        for (Instance instance : instances)
        {
            labelCounts[getLabelIndex(instance.label)]++;
        }
        double entropy = 0;
        for (int i = 0; i < labelCounts.length; i++)
        {
            if (totalInstances != 0 && labelCounts[i] != 0)
            {
                double probability = Double.valueOf(labelCounts[i]) / Double.
                        valueOf(totalInstances);
                entropy += probability * Math.log10(probability) / Math.log10(2);
            }
        }
        return -entropy;
    }

    private double calculateConditionalEntropy(String attribute, List<Instance> instances)
    {
        int noOfAtrributeTypes = attributeValues.get(attribute).size();
        int[] labelCounts = new int[noOfAtrributeTypes];
        ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
        //Create empty lists
        for (int i = 0; i < noOfAtrributeTypes; i++)
        {
            labelInstances.add(new ArrayList<Instance>());
        }
        int totalInstances = instances.size();
        for (Instance instance : instances)
        {
            labelCounts[getAttributeValueIndex(attribute, instance.attributes.
                    get(getAttributeIndex(attribute)))]++;
            labelInstances.
                    get(getAttributeValueIndex(attribute, instance.attributes.
                            get(getAttributeIndex(attribute)))).
                    add(instance);
        }
        double conditionalEntropy = 0;
        for (int i = 0; i < labelCounts.length; i++)
        {
            if (totalInstances != 0 && labelCounts[i] != 0)
            {
                double probability = Double.valueOf(labelCounts[i]) / Double.
                        valueOf(totalInstances);
                double subConditionalEntropy = calculateEntropy(labelInstances.
                        get(i));
                conditionalEntropy += probability * subConditionalEntropy;
            }
        }

        return conditionalEntropy;
    }

    private String getMajority(List<Instance> instances)
    {
        assert (instances.size() > 0);
        int[] labelCounts = new int[labels.size()];
        for (Instance instance : instances)
        {
            labelCounts[getLabelIndex(instance.label)]++;
        }
        int majorityCanidate = -1;
        int canidateCount = -1;
        for (int i = 0; i < labelCounts.length; i++)
        {
            if (labelCounts[i] > canidateCount)
            {
                majorityCanidate = i;
                canidateCount = labelCounts[i];
            }
        }
        return labels.get(majorityCanidate);
    }

    private boolean isPure(List<Instance> instances)
    {
        int canidate = -1;
        for (Instance instance : instances)
        {
            if (canidate == -1)
            {
                canidate = getLabelIndex(instance.label);
                continue;
            }
            if (canidate != getLabelIndex(instance.label))
            {
                return false;
            }
        }
        return true;
    }

    private void buildTree(DecTreeNode parent, List<Instance> instances, List<String> attributesLeft, String attributeValue, String defaultClass)
    {
        DecTreeNode currentNode = null;
        String highestEntropyAttribute = getAttributeToSplitBy(instances, attributesLeft);
        if (parent == null)
        {
            root = new DecTreeNode(getMajority(instances), highestEntropyAttribute, "ROOT", isPure(instances));
            if (isPure(instances))
            {
                return;
            }
            currentNode = root;
        }
        if (instances.isEmpty())
        {
            parent.
                    addChild(new DecTreeNode(defaultClass, "", attributeValue, true));
            return;
        }
        if (attributesLeft.isEmpty())
        {
            parent.
                    addChild(new DecTreeNode(getMajority(instances), "", attributeValue, true));
            return;
        }

        if (isPure(instances))
        {
            parent.
                    addChild(new DecTreeNode(getMajority(instances), "", attributeValue, true));
            return;
        }

        if (parent != null)
        {
            currentNode = new DecTreeNode(getMajority(instances), highestEntropyAttribute, attributeValue, isPure(instances) || attributesLeft.
                    size() == 0);
            parent.addChild(currentNode);
        }

        int noOfChildren = attributeValues.get(highestEntropyAttribute).size();
        ArrayList<ArrayList<Instance>> labelInstances = new ArrayList<ArrayList<Instance>>();
        for (int i = 0; i < noOfChildren; i++)
        {
            labelInstances.add(new ArrayList<Instance>());
        }
        for (Instance instance : instances)
        {
            labelInstances.
                    get(getAttributeValueIndex(highestEntropyAttribute, instance.attributes.
                            get(getAttributeIndex(highestEntropyAttribute)))).
                    add(instance);
        }
        for (int i = 0; i < noOfChildren; i++)
        {
            attributesLeft.remove(attributesLeft.
                    indexOf(highestEntropyAttribute));
            buildTree(currentNode, labelInstances.get(i), attributesLeft, attributeValues.
                    get(highestEntropyAttribute).get(i), getMajority(instances));
            attributesLeft.add(highestEntropyAttribute);
        }
    }

    private String getAttributeToSplitBy(List<Instance> instances, List<String> attributes)
    {
        double entropy = calculateEntropy(instances);
        String highestEntropyAttribute = "";
        double highestMutualInfo = -1;
        for (int i = 0; i < attributes.size(); i++)
        { //For each attribute
            double conditionalEntropy = calculateConditionalEntropy(attributes.
                    get(i), instances);
            double mutualInformation = entropy - conditionalEntropy;
            if (mutualInformation > highestMutualInfo)
            {
                highestMutualInfo = mutualInformation;
                highestEntropyAttribute = attributes.get(i);
            }
        }
        return highestEntropyAttribute;
    }

    private double calculateAccuracy(DataSet tune)
    {
        int correctClassification = 0;
        for (Instance instance : tune.instances)
        {
            if (classify(instance).equals(tune.labels.
                    get(getLabelIndex(instance.label))))
            {
                correctClassification++;
            }
        }
        double accuracy = Double.valueOf(correctClassification) / Double.
                valueOf(tune.instances.size());
        return accuracy;
    }
}
