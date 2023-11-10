package persistence.sql.metadata.association;

import java.lang.reflect.Field;

public enum AssociationType {
	OneToMany {
		@Override
		public Association createdAssociation(Field field) {
			return new OneToMany(field);
		}
	},
	ManyToOne {
		@Override
		public Association createdAssociation(Field field) {
			return null;
		}
	};

	public abstract Association createdAssociation(Field field);
}
