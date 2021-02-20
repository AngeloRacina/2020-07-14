package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	
	private Map<Integer, Team> idMap;
	private Graph<Team, DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao ;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.idMap = new HashMap<Integer, Team >();
		this.dao.listAllTeams(idMap);
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//add vertices
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		for(Classifica c1 : this.getEdges()) {
			for(Classifica c2 : this.getEdges()) {
				if(!c1.getTeam().equals(c2.getTeam())) {
					if((c1.getPunteggio()-c2.getPunteggio())>0) {
						if(!this.grafo.containsEdge(this.grafo.getEdge(c1.getTeam(), c2.getTeam()))) {
							Graphs.addEdge(this.grafo, c1.getTeam(), c2.getTeam(), c1.getPunteggio()-c2.getPunteggio());
						}
					}
				}
			}
		}
	}
	
	
	public List<Classifica> getEdges(){
		
		Map<Integer, Classifica> mappa = new HashMap<Integer, Classifica>();
		
		for(Match m : this.dao.listAllMatches()) {
			if(m.getResultOfTeamHome() == 1) {
				if(mappa.containsKey(m.getTeamHomeID())){
					mappa.get(m.getTeamHomeID()).incrementaPunteggio(3);
				}else if(!mappa.containsKey(m.getTeamHomeID())) {
					mappa.put(m.getTeamHomeID(), new Classifica(idMap.get(m.getTeamHomeID()), 3));
				}
					
			}else if(m.getReaultOfTeamHome() ==0) {
				if(mappa.containsKey(m.getTeamHomeID())){
					mappa.get(m.getTeamHomeID()).incrementaPunteggio(1);
				}else if(!mappa.containsKey(m.getTeamHomeID())) {
					mappa.put(m.getTeamHomeID(), new Classifica(idMap.get(m.getTeamHomeID()), 1));
				}
			}else if(m.getReaultOfTeamHome() == -1){
				if(mappa.containsKey(m.getTeamHomeID())){
					mappa.get(m.getTeamHomeID()).incrementaPunteggio(0);
				}else if(!mappa.containsKey(m.getTeamHomeID())) {
					mappa.put(m.getTeamHomeID(), new Classifica(idMap.get(m.getTeamHomeID()), 0));
				}	
			}
		}
		
		for(Match m : this.dao.listAllMatches()) {
			if(m.getResultOfTeamHome() == 1) {
				if(mappa.containsKey(m.getTeamAwayID())) {
					mappa.get(m.getTeamAwayID()).incrementaPunteggio(0);
				}else if (!mappa.containsKey(m.getTeamAwayID())) {
					mappa.put(m.getTeamAwayID(), new Classifica(idMap.get(m.getTeamAwayID()), 0));
				}
					
			}else if(m.getReaultOfTeamHome() ==0) {
				if(mappa.containsKey(m.getTeamAwayID())) {
					mappa.get(m.getTeamAwayID()).incrementaPunteggio(1);
				}else if (!mappa.containsKey(m.getTeamAwayID())) {
					mappa.put(m.getTeamAwayID(), new Classifica(idMap.get(m.getTeamAwayID()), 1));
				}
			}else if(m.getReaultOfTeamHome() == -1){
				if(mappa.containsKey(m.getTeamAwayID())) {
					mappa.get(m.getTeamAwayID()).incrementaPunteggio(3);
				}else if (!mappa.containsKey(m.getTeamAwayID())) {
					mappa.put(m.getTeamAwayID(), new Classifica(idMap.get(m.getTeamAwayID()), 3));
				}	
			}
		}
		
		List<Classifica> result = new ArrayList<>(mappa.values());
		Collections.sort(result);
		
		return result;
	}
	
	public List<Team> getTeams(){
		return new ArrayList<Team>(this.grafo.vertexSet());
	}
	
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	
	public int nEdges() {
		return this.grafo.edgeSet().size();
	}

	public List<Classifica> getSquadreMigliori(Team t) {
		// TODO Auto-generated method stub
		List<Classifica> result = new ArrayList<Classifica>();
		int punteggio = this.getPunteggioTeam(t);
		
		for(Classifica c : this.getEdges())
			if(punteggio < c.getPunteggio())
				result.add(new Classifica( c.getTeam(), c.getPunteggio()-punteggio));
		
		Collections.sort(result);
		return result;
	}

	public List<Classifica> getSquadrePeggiori(Team t) {
		// TODO Auto-generated method stub
		List<Classifica> result = new ArrayList<Classifica>();
		int punteggio = this.getPunteggioTeam(t);
		
		for(Classifica c : this.getEdges())
			if(punteggio > c.getPunteggio())
				result.add(new Classifica( c.getTeam(), punteggio-c.getPunteggio()));
		
		Collections.sort(result);
		return result;
	}

	private int getPunteggioTeam(Team t) {
		// TODO Auto-generated method stub
		int punteggio = 0;
		for(Classifica c : this.getEdges()) {
			if(c.getTeam().equals(t))
				punteggio = c.getPunteggio();
		}
		return punteggio;
	}
}
