package com.sahyadri.sahyadripooltrip.service;
import com.sahyadri.sahyadripooltrip.entity.BikeRoadTrip;
import com.sahyadri.sahyadripooltrip.repository.BikeRoadTripRepository;
import org.springframework.core.io.ClassPathResource; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.io.*; import java.nio.charset.StandardCharsets; import java.util.*;
@Service public class BikeRoadTripCsvImportService {
 private static final String FILE="data/INDIA_BIKE_ROAD_TRIPS_MASTER.csv"; private final BikeRoadTripRepository repo;
 public BikeRoadTripCsvImportService(BikeRoadTripRepository r){repo=r;}
 @Transactional public int importAll() throws Exception {ClassPathResource res=new ClassPathResource(FILE);if(!res.exists())throw new IllegalStateException("Bike route CSV not found: "+FILE);int count=0;try(BufferedReader br=new BufferedReader(new InputStreamReader(res.getInputStream(),StandardCharsets.UTF_8))){String h=br.readLine();if(h==null)throw new IllegalStateException("CSV is empty");String line;int row=1;while((line=br.readLine())!=null){row++;if(line.isBlank())continue;List<String> c=parse(line);if(c.size()!=13)throw new IllegalStateException("Invalid bike route CSV line "+row+" expected 13 columns, got "+c.size());BikeRoadTrip b=repo.findById(v(c,0)).orElseGet(BikeRoadTrip::new);b.setRouteId(v(c,0));b.setRouteName(v(c,1));b.setStartPoint(v(c,2));b.setEndPoint(v(c,3));b.setStates(v(c,4));b.setRouteDistanceKm(num(v(c,5)));b.setDifficulty(v(c,6));b.setBestSeason(v(c,7));b.setHighlights(v(c,8));b.setPhotoQuery(v(c,9));b.setPrimarySource(v(c,10));b.setVerificationStatus(v(c,11));b.setRecordStatus(v(c,12));repo.save(b);count++;}}return count;}
 private String v(List<String> c,int i){String s=c.get(i);return s==null||s.trim().isEmpty()?null:s.trim();} private Double num(String s){try{return s==null?null:Double.parseDouble(s);}catch(Exception e){return null;}}
 private List<String> parse(String l){List<String>a=new ArrayList<>();StringBuilder b=new StringBuilder();boolean q=false;for(int i=0;i<l.length();i++){char ch=l.charAt(i);if(ch=='"'){if(q&&i+1<l.length()&&l.charAt(i+1)=='"'){b.append('"');i++;}else q=!q;}else if(ch==','&&!q){a.add(b.toString());b.setLength(0);}else b.append(ch);}a.add(b.toString());return a;}
}
