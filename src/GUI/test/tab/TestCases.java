package GUI.test.tab;

enum TestCases {
    PARAMETER_ACCURACY_TEST("Parameter Accuracy Test", "Test the accuracy of predefined parameters based on the data range"),
    DEPTH_TEST("Depth Test", "Test the multiplicative and additive depth of the given parameters");

    private final String name;
    private final String tooltip;

    TestCases(String name, String tooltip) {
        this.name = name;
        this.tooltip = tooltip;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    @Override
    public String toString() {
        return name;
    }
}