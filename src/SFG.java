import java.util.*;

public class SFG {

    private Hashtable<String, HashMap<String, Double>> graph;
    private String inputVertex;
    private String outputVertex;
    private Set<String> vertices;
    private ArrayList<ArrayList<String>> forwardPaths;
    private ArrayList<ArrayList<String>> cycles;

    SFG(){
        graph = new Hashtable<String, HashMap<String, Double>>();
        forwardPaths = new ArrayList<ArrayList<String>>();
        cycles = new ArrayList<ArrayList<String>>();
    }

    public void addEdge(String source, String to, double weight){
        if (!graph.containsKey(source)){
            graph.put(source,new HashMap<>());
        }
        if (!graph.containsKey(to)){
            graph.put(to,new HashMap<>());
        }
        HashMap<String, Double> nodes = graph.get(source);
        nodes.put(to,weight);
    }

    private void dfs(String u, Set<String> visited) {
        visited.add(u);
        for(Map.Entry<String, Double> node : graph.get(u).entrySet()){
            if (!visited.contains(node.getKey())){
                dfs(node.getKey(), visited);
            }
        }
    }

    public boolean checkConnected() {
        Set<String> visited = new HashSet<>();
        dfs(inputVertex,visited);
        return graph.size() == visited.size();
    }

    public boolean checkOut() {
        vertices = graph.keySet();
        int counter = 0;
        for (String vertex : vertices) {
            if (graph.get(vertex).size() == 0) {
                counter++;
                outputVertex = vertex;
            }
        }
        return counter == 1;
    }

    public boolean checkIn() {
        Set<String> visited = new HashSet<>();
        for (String vertex : vertices) {
            for (Map.Entry<String, Double> node : graph.get(vertex).entrySet()) {
                visited.add(node.getKey());
            }
        }
        if (graph.size() -  visited.size() == 1) {
            for(String vertex : vertices) {
                if (!visited.contains(vertex)) {
                    inputVertex = vertex;
                    return true;
                }
            }
        }
        return false;
    }

    private void dfsOnPaths(String u,ArrayList<String> visited) {
        if (u.equals(outputVertex)) {
            visited.add(u);
            ArrayList<String> newPath = new ArrayList<>(visited);
            forwardPaths.add(newPath);
            return;
        }
        int index = visited.indexOf(u);
        if (index != -1) {
            ArrayList<String> newPath = new ArrayList<>();
            visited.add(u);
            for (int i = index; i < visited.size(); i++) {
                newPath.add(visited.get(i));
            }
            cycles.add(newPath);
            return;
        }
        visited.add(u);
        for(Map.Entry<String, Double> node: graph.get(u).entrySet()) {
            dfsOnPaths(node.getKey(),visited);
            visited.remove(visited.size()-1);
        }
    }

    private void duplicate(ArrayList<ArrayList<String>> arr) {
        for (int i = 0; i < arr.size(); i++) {
            for (int j = i+1; j < arr.size(); j++) {
                if (arr.get(i).size() != arr.get(j).size())
                    continue;
                boolean duplicate = true;
                for (int k = 0; k < arr.get(i).size(); k++) {
                    if (!arr.get(j).contains(arr.get(i).get(k))) {
                        duplicate = false;
                        break;
                    }
                }
                if (duplicate) {
                    arr.remove(j);
                    j--;
                }
            }
        }
    }

    private void findPaths(){
        ArrayList<String> visited = new ArrayList<>();
        dfsOnPaths(inputVertex,visited);
        duplicate(cycles);
        duplicate(forwardPaths);
    }

    private boolean intersection(ArrayList<String> arr1, ArrayList<String> arr2) {
        for(String node1 : arr1){
            for(String node2 : arr2){
                if (node1.equals(node2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void makeCollection(boolean[][] nonTouched) {
        for (int i = 0; i < cycles.size(); i++) {
            for (int j = i+1; j < cycles.size(); j++) {
                if (!intersection(cycles.get(i),cycles.get(j))) {
                    nonTouched[i][j] = true; nonTouched[j][i] = true;
                }
            }
        }
    }

    private double getWeight(ArrayList<String> arr){
        double value = 1;
        for (int i = 0; i < arr.size()-1; i++) {
            value *= graph.get(arr.get(i)).get(arr.get(i+1));
        }
        return value;
    }

    private void combination(int arr[], int[] data, int start, int end, int index, int r, boolean[][] nonTouched, ArrayList<ArrayList<Integer>> allNonTouched){
        if (index == r) {
            boolean check = true;
            for (int i = 0; i < r; i++) {
                for (int j = i+1; j < r; j++) {
                    if (!nonTouched[data[i]][data[j]]) {
                        check = false;
                        break;
                    }
                }
                if (!check) break;
            }
            if (check) {
                ArrayList<Integer> arrayList = new ArrayList<>();
                for (int x : data){
                    arrayList.add(x);
                }
                allNonTouched.add(arrayList);
            }
            return;
        }
        for (int i = start; i <= end && end - i + 1 >= r-index; i++){
            data[index] = arr[i];
            combination(arr, data, i+1,end, index+1, r, nonTouched, allNonTouched);
        }
    }

    private double[] calculateDelta(ArrayList<ArrayList<Integer>> allNonTouched) {
        double[] res = new double[1+forwardPaths.size()];
        ArrayList<Double> weightedCycles = new ArrayList<>();
        for (ArrayList<String> cycle : cycles) {
            weightedCycles.add(getWeight(cycle));
        }
        for(int i = 0; i < weightedCycles.size(); i++) {
            res[0] += weightedCycles.get(i);
            for (int j = 0; j < forwardPaths.size(); j++) {
                if (!intersection(forwardPaths.get(j),cycles.get(i))) {
                    res[j+1] += weightedCycles.get(i);
                }
            }
        }
        for(ArrayList<Integer> nonTouched : allNonTouched) {
            double value = 1;
            for(int indexOfCycles : nonTouched){
                value *= weightedCycles.get(indexOfCycles);
            }
            value *= Math.pow(-1,nonTouched.size()+1);
            res[0] += value;
            int i = 1;
            for(ArrayList<String> forwardPath: forwardPaths) {
                boolean intersectWithPath = false;
                for(int indexOfCycles : nonTouched){
                    if (intersection(forwardPath,cycles.get(indexOfCycles))){
                        intersectWithPath = true;
                        break;
                    }
                }
                if (!intersectWithPath) {
                    res[i] += value;
                }
                i++;
            }
        }
        return res;
    }

    public double cal() {
        findPaths();
        boolean[][] nonTouched = new boolean[cycles.size()][cycles.size()];
        int[] arr = new int[cycles.size()];
        for (int i = 0; i < cycles.size(); i++) {
            for (int j = 0; j < cycles.size(); j++) {
                nonTouched[i][j] = false;
            }
            arr[i] = i;
        }
        ArrayList<ArrayList<Integer>> allNonTouched = new ArrayList<>();
        makeCollection(nonTouched);
        for (int i = 2; i <= cycles.size(); i++) {
            int[] data = new int[i];
            combination(arr,data,0,cycles.size()-1,0,i,nonTouched,allNonTouched);
            if (allNonTouched.size() == 0 || allNonTouched.get(allNonTouched.size()-1).size() < i)
                break;
        }
        double[] delta = calculateDelta(allNonTouched);
        for (int i = 0; i < delta.length; i++) {
            delta[i] = 1- delta[i];
        }
        double result = 0;
        for (int i = 0; i < forwardPaths.size(); i++) {
            result += delta[i+1]*getWeight(forwardPaths.get(i));
        }
        return result/delta[0];
    }

}
