package generation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pro.taskana.data.generation.builder.WorkbasketStructureBuilder;
import pro.taskana.data.generation.util.ElementStack;
import pro.taskana.data.generation.util.Formatter;
import pro.taskana.impl.WorkbasketWrapper;

public class TestWorkbasketStructureGeneration {

    private static final String DOMAIN = "T";
    private WorkbasketStructureBuilder domainBuilder;

    @Before
    public void setUp() {
        domainBuilder = new WorkbasketStructureBuilder(DOMAIN);
    }

    @Test
    public void shouldVerifyStructureWithDepth1() {
        List<WorkbasketWrapper> workbaskets = domainBuilder.createSimpleWorkbaskets(6).toList();

        // Necessary to force WorkbasketWrapper for building valid workbaskets
        domainBuilder.getGeneratedWorkbaskets();

        for (int i = 0; i < workbaskets.size(); i++) {
            WorkbasketWrapper wbLvl1 = workbaskets.get(i);
            String ecpectedOrgLvl1 = Formatter.format(i + 1, WorkbasketWrapper.NUMBER_LENGTH_IN_ID);
            String expectedOwnerLvl1 = getExpectedOwner(null, wbLvl1, ecpectedOrgLvl1);

            assertThat(wbLvl1.getOwner(), equalTo(expectedOwnerLvl1));
            assertThat(wbLvl1.getOrgLevel1(), equalTo(ecpectedOrgLvl1));
        }
    }

    @Test
    public void shouldVerifyStructureWithDepth2() {
        ElementStack<WorkbasketWrapper> workbasketsLvl2 = domainBuilder.createSimpleWorkbaskets(4);
        List<WorkbasketWrapper> workbasketsLvl1 = domainBuilder.newLayer().withWb(2).withNumberOfDistTargets(2)
                .selectFrom(workbasketsLvl2).build();

        // Necessary to force WorkbasketWrapper for building valid workbaskets
        domainBuilder.getGeneratedWorkbaskets();
        workbasketsLvl1.sort(Comparator.comparing(x -> Integer.parseInt(x.getOrgLevel1())));

        for (int i = 0; i < workbasketsLvl1.size(); i++) {
            WorkbasketWrapper wbLvl1 = workbasketsLvl1.get(i);
            String expectedOrgLvl1 = Formatter.format(i + 1, WorkbasketWrapper.NUMBER_LENGTH_IN_ID);

            for (int j = 0; j < wbLvl1.getDirectChildren().size(); j++) {
                WorkbasketWrapper wbLvl2 = wbLvl1.getDirectChildren().get(j);

                String compoundOrgLvlWbLvl2 = expectedOrgLvl1 + Formatter.format(j + 1, 2);
                String expectedOwnerLvl2 = getExpectedOwner(wbLvl1, wbLvl2, compoundOrgLvlWbLvl2);

                assertThat(wbLvl2.getOwner(), equalTo(expectedOwnerLvl2));
                assertThat("OrgLvl1", wbLvl2.getOrgLevel1(), equalTo(wbLvl1.getOrgLevel1()));
                assertThat("OrgLvl2", wbLvl2.getOrgLevel2(), equalTo(Formatter.format(j + 1, 2)));
            }
        }
    }

    @Ignore
    @Test
    public void shouldVerifyStructureWithDepth3() {
        ElementStack<WorkbasketWrapper> workbasketsLvl3 = domainBuilder.createSimpleWorkbaskets(8);
        List<WorkbasketWrapper> workbasketsLvl2 = domainBuilder.newLayer().withWb(4).withNumberOfDistTargets(2)
                .selectFrom(workbasketsLvl3).build();
        List<WorkbasketWrapper> workbasketsLvl1 = domainBuilder.newLayer().withWb(2).withNumberOfDistTargets(2)
                .selectFrom(new ElementStack<>(workbasketsLvl2)).build();

        // Necessary to force WorkbasketWrapper for building workbaskets
        domainBuilder.getGeneratedWorkbaskets();
        workbasketsLvl1.sort(Comparator.comparing(x -> Integer.parseInt(x.getOrgLevel1())));

        for (int i = 0; i < workbasketsLvl1.size(); i++) {
            WorkbasketWrapper wbLvl1 = workbasketsLvl1.get(i);
            String expectedOrgLvl1 = Formatter.format(i + 1, WorkbasketWrapper.NUMBER_LENGTH_IN_ID);

            for (int j = 0; j < wbLvl1.getDirectChildren().size(); j++) {
                WorkbasketWrapper wbLvl2 = wbLvl1.getDirectChildren().get(j);
                String expectedOrgLvl2 = Formatter.format(wbLvl1.getMemberId(), WorkbasketWrapper.NUMBER_LENGTH_IN_ID);
                String compoundOrgLvlWbLvl2 = expectedOrgLvl1 + expectedOrgLvl2;

                for (int k = 0; k < wbLvl2.getDirectChildren().size(); k++) {
                    WorkbasketWrapper wbLvl3 = wbLvl2.getDirectChildren().get(k);

                    String expectedOrgLvl3 = Formatter.format(wbLvl2.getMemberId(),
                            WorkbasketWrapper.NUMBER_LENGTH_IN_ID);
                    String compoundOrgLvlWbLvl3 = compoundOrgLvlWbLvl2 + expectedOrgLvl3;
                    String expectedOwnerLvl3 = getExpectedOwner(wbLvl2, wbLvl3, compoundOrgLvlWbLvl3);

                    assertThat(wbLvl3.getOwner(), equalTo(expectedOwnerLvl3));
                    assertThat("OrgLvl1", wbLvl3.getOrgLevel1(), equalTo(wbLvl2.getOrgLevel1()));
                    assertThat("OrgLvl2", wbLvl3.getOrgLevel2(), equalTo(wbLvl2.getOrgLevel2()));
                    assertThat("OrgLvl3", wbLvl3.getOrgLevel3(), equalTo(expectedOrgLvl3));
                }
            }
        }

    }

    @Ignore
    @Test
    public void shouldVerifyStructureWithDepth4() {
        ElementStack<WorkbasketWrapper> workbasketsLvl4 = domainBuilder.createSimpleWorkbaskets(8);
        List<WorkbasketWrapper> xd = workbasketsLvl4.toList();
        List<WorkbasketWrapper> workbasketsLvl3 = domainBuilder.newLayer().withWb(4).withNumberOfDistTargets(2)
                .selectFrom(workbasketsLvl4).build();
        List<WorkbasketWrapper> workbasketsLvl2 = domainBuilder.newLayer().withWb(2).withNumberOfDistTargets(2)
                .selectFrom(new ElementStack<>(workbasketsLvl3)).build();
        List<WorkbasketWrapper> workbasketsLvl1 = domainBuilder.newLayer().withWb(1).withNumberOfDistTargets(2)
                .selectFrom(new ElementStack<>(workbasketsLvl2)).build();

        // Necessary to force WorkbasketWrapper for building workbaskets
        domainBuilder.getGeneratedWorkbaskets();
        workbasketsLvl1.sort(Comparator.comparing(x -> Integer.parseInt(x.getOrgLevel1())));

        System.out.println("Root");
        print(workbasketsLvl1);
        System.out.println("Level1");
        print(workbasketsLvl2);
        System.out.println("Level2");
        print(workbasketsLvl3);
        System.out.println("Blätter");
        print(xd);

        for (WorkbasketWrapper workbasketWrapper1 : workbasketsLvl1) {
            System.out.println("------------");
            System.out.println(workbasketWrapper1.getKey() + "   " + workbasketWrapper1.getOwnerAsUser().getId());
            for (WorkbasketWrapper workbasketWrapper2 : workbasketWrapper1.getDirectChildren()) {
                System.out.println("------------");
                System.out.println(workbasketWrapper2.getKey() + "   " + workbasketWrapper2.getOwnerAsUser().getId());
                for (WorkbasketWrapper workbasketWrapper3 : workbasketWrapper2.getDirectChildren()) {
                    System.out
                            .println(workbasketWrapper3.getKey() + "   " + workbasketWrapper3.getOwnerAsUser().getId());
                    for (WorkbasketWrapper workbasketWrapper4 : workbasketWrapper3.getDirectChildren()) {
                        System.out.println(
                                workbasketWrapper4.getKey() + "   " + workbasketWrapper4.getOwnerAsUser().getId());
                    }
                }
            }
        }

        for (int i = 0; i < workbasketsLvl1.size(); i++) {
            WorkbasketWrapper wbLvl1 = workbasketsLvl1.get(i);
            String expectedOrgLvlWbLvl1 = Formatter.format(i, WorkbasketWrapper.NUMBER_LENGTH_IN_ID);

            for (int j = 0; j < wbLvl1.getDirectChildren().size(); j++) {
                WorkbasketWrapper wbLvl2 = wbLvl1.getDirectChildren().get(j);
                String expectedOrgLvlWbLvl2 = expectedOrgLvlWbLvl1
                        + Formatter.format(wbLvl1.getMemberId(), WorkbasketWrapper.NUMBER_LENGTH_IN_ID);

                for (int k = 0; k < wbLvl2.getDirectChildren().size(); k++) {
                    WorkbasketWrapper wbLvl3 = wbLvl2.getDirectChildren().get(k);
                    String expectedOrgLvlWbLvl3 = expectedOrgLvlWbLvl2
                            + Formatter.format(wbLvl2.getMemberId(), WorkbasketWrapper.NUMBER_LENGTH_IN_ID);

                    for (int l = 0; l < wbLvl3.getDirectChildren().size(); l++) {
                        WorkbasketWrapper wbLvl4 = wbLvl3.getDirectChildren().get(l);

                        String expectedOrgLvl4 = Formatter.format(wbLvl3.getMemberId(),
                                WorkbasketWrapper.NUMBER_LENGTH_IN_ID);
                        String compoundOrgLvlWbLvl4 = expectedOrgLvlWbLvl3 + expectedOrgLvl4;
                        String expectedOwnerLvl4 = getExpectedOwner(wbLvl3, wbLvl4, compoundOrgLvlWbLvl4);

                        assertThat(wbLvl4.getOwner(), equalTo(expectedOwnerLvl4));
                        assertThat("OrgLvl1", wbLvl4.getOrgLevel1(), equalTo(String.valueOf(wbLvl3.getOrgLevel1())));
                        assertThat("OrgLvl2", wbLvl4.getOrgLevel2(), equalTo(String.valueOf(wbLvl3.getOrgLevel2())));
                        assertThat("OrgLvl3", wbLvl4.getOrgLevel3(), equalTo(String.valueOf(wbLvl3.getOrgLevel3())));
                        assertThat("OrgLvl4", wbLvl4.getOrgLevel4(), equalTo(expectedOrgLvl4));
                    }
                }
            }
        }
    }

    private void print(List<WorkbasketWrapper> t) {
        for (int i = 0; i < t.size(); i++) {
            System.out.println(t.get(i).getKey());

        }
    }

    private String getExpectedOwner(WorkbasketWrapper parent, WorkbasketWrapper child, String compoundOrgLvl) {
        String expectedOwner = null;
        if (parent != null && child.getOwner().equals(parent.getOwner())) {
            expectedOwner = parent.getOwner();
        } else {
            expectedOwner = DOMAIN + "U" + compoundOrgLvl;
        }
        return expectedOwner;
    }

}
