package pro.taskana.export;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class DataExporter<T> {

    private final List<T> objects;
    
    private List<Function<T, String>> producer;
    private List<Predicate<T>> predicates;
    private List<Supplier<String>> supplier;
    private List<String> constantLineValues;
    private Integer maxLines;
    
    public DataExporter(List<T> objects) {
        this.objects = objects;
        this.producer = new ArrayList<>();
        this.predicates = new ArrayList<>();
        this.supplier = new ArrayList<>();
        this.maxLines = null;
        this.constantLineValues = new ArrayList<>();
    }
    
    public void addLineValueProducer(Function<T, String> producer) {
        this.producer.add(producer);
    }
    
    public void maxLines(Integer maxLines) {
        this.maxLines = maxLines;
    }
    
    public void addConstantLineValue(String value) {
        this.constantLineValues.add(value);
    }
    
    public void addLineValueSupplier(Supplier<String> supplier) {
        this.supplier.add(supplier);
    }
    
    public void addPredicate(Predicate<T> predicate) {
        this.predicates.add(predicate);
    }
    
    public List<List<String>> generateFileContent() {                
        List<T> filteredObjects = objects.stream()
                .filter(o -> predicates.stream().allMatch(p -> p.test(o))).collect(Collectors.toList());
        if(maxLines != null && filteredObjects.size() > maxLines) {
            filteredObjects = filteredObjects.subList(0, maxLines);
        }
        List<List<String>> lines = filteredObjects.stream()
                .map(o -> producer.stream().map(p -> p.apply(o)).collect(Collectors.toList()))
                .collect(Collectors.toList());
        lines.forEach(line -> line.addAll(constantLineValues));
        return lines;
    }
    
    
    
    
}
