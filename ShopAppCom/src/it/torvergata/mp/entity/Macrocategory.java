package it.torvergata.mp.entity;

public class Macrocategory {
		private int id;
		private String name;
		
		
		public Macrocategory(int i,String n){
			id=i;
			name=n;	
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
}
