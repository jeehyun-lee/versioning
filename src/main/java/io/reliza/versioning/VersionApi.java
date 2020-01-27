/**
* Copyright 2019 Reliza Incorporated. Licensed under MIT License.
* https://reliza.io
*/

package io.reliza.versioning;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * This class contains static methods to use for higher level versioning API
 *
 */
public class VersionApi {
	/**
	 * This static subclass is used to pass raw objects for versioning into Version API
	 *
	 */
	public static class VersionApiObject {
		private String schema;
		private String modifier;
		private String metadata;
		private String version;
		
		/**
		 * Private constructor to initialize VersionApiObject based on specified schema
		 * @param schema String
		 */
		private VersionApiObject (String schema) {
			this.schema = schema;
		}
		
		/**
		 * Getter for VAO schema
		 * @return schema String
		 */
		public String getSchema() {
			return schema;
		}
		
		/**
		 * Setter for VAO schema
		 * @param schema String
		 */
		public void setSchema(String schema) {
			this.schema = schema;
		}
		
		/**
		 * Getter for VAO modifier
		 * @return modifier String
		 */
		public String getModifier() {
			return modifier;
		}
		
		/**
		 * Setter for VAO modifier
		 * @param modifier String
		 */
		public void setModifier(String modifier) {
			this.modifier = modifier;
		}
		
		/**
		 * Getter for VAO metadata
		 * @return metadata String
		 */
		public String getMetadata() {
			return metadata;
		}
		
		/**
		 * Setter for VAO metadata
		 * @param metadata String
		 */
		public void setMetadata(String metadata) {
			this.metadata = metadata;
		}
		
		/**
		 * Getter for VAO version
		 * @return version String
		 */
		public String getVersion() {
			return version;
		}
		
		/**
		 * Setter for VAO version
		 * @param version String
		 */
		public void setVersion(String version) {
			this.version = version;
		}
		
	}

	/**
	 * Enum to store available api actions
	 *
	 */
	public static enum ActionEnum {
		BUMP("bump"),
		BUMP_MINOR("bumpminor"),
		BUMP_MAJOR("bumpmajor"),
		BUMP_DATE("bumpdate")
		;
		
		private String actionName;
		
		private final static Map<String, ActionEnum> aLookupMap;
		
		static {
			aLookupMap = new HashMap<>();
			for (ActionEnum ae : ActionEnum.values()) {
				aLookupMap.put(ae.getActionName(), ae);
			}
		}
		
		/**
		 * Private constructor for ActionEnum
		 * @param actionName String, for use i.e. in CLI apis
		 */
		private ActionEnum (String actionName) {
			this.actionName = actionName;
		}
		
		/**
		 * Getter for action name string
		 * @return action name String
		 */
		public String getActionName () {
			return this.actionName;
		}
		
		/**
		 * Lookup method to get ActionEnum by its name
		 * @param name String
		 * @return ActionEnum element
		 */
		public static ActionEnum getActionEnum (String name) {
			ActionEnum retAe = null;
			if (StringUtils.isNotEmpty(name)) {
				retAe = aLookupMap.get(name.toLowerCase());
			}
			return retAe;
		}
	}
	
	/**
	 * Factory method to create VersionApiObject based on schema
	 * @param schema String
	 * @return VersionApiObject class to be consumed by apis
	 */
	public static VersionApiObject createVao (String schema) {
		return new VersionApiObject(schema);
	}
	
	/**
	 * Factory method to initialize version based on VersionApiObject
	 * @param vao VersionApiObject
	 * @return Version object
	 */
	public static Version initializeVersion (VersionApiObject vao) {
		Version v = null;
		if (StringUtils.isEmpty(vao.getVersion())) {
			v = VersionUtils.initializeVersionWithModMeta(vao.getSchema(),
										vao.getModifier(), vao.getMetadata());
		} else {
			v = new Version(vao.getVersion(), vao.getSchema());
			if (StringUtils.isNotEmpty(vao.getModifier())) {
				// if empty we might want to use the one from current version
				v.setModifier(vao.getModifier());
			}
			v.setMetadata(vao.getMetadata());
		}
		return v;
	}
	
	/**
	 * Method to initialize specifically SemVer version given version string
	 * @param version String
	 * @return
	 */
	public static Version initializeSemVerVersion (String version) {
		VersionApiObject vao = createVao(Constants.SEMVER);
		vao.setVersion(version);
		return initializeVersion(vao);
	}
	
	/**
	 * This method applies an Action on a given Version object and mutates that Version object
	 * @param v Version
	 * @param ae ActionEnum
	 */
	public static void applyActionOnVersion (Version v, ActionEnum ae) {
		if (ActionEnum.BUMP == ae) {
			v.simpleBump();
		} else if (ActionEnum.BUMP_MINOR == ae) {
			v.bumpMinor(null);
		} else if (ActionEnum.BUMP_MAJOR == ae) {
			v.bumpMajor(null);
		} else if (ActionEnum.BUMP_DATE == ae) {
			v.setCurrentDate();
		}
	}
	
	/**
	 * This method mutates version and sets it's major, minor and patch elements to same 
	 * values as what provided semver version has
	 * @param v Version object that will be mutated
	 * @param semver Version string, must conform to SemVer (Major.Minor.Patch) schema
	 */
	public static void setSemVerElementsOnVersion (Version v, String semver) {
		Version semVerVersion = initializeSemVerVersion(semver);
		v.setMajor(semVerVersion.getMajor());
		v.setMinor(semVerVersion.getMinor());
		v.setPatch(semVerVersion.getPatch());
	}
	
	/**
	 * This method applies an Action defined by its String name
	 * on a given Version object and mutates that Version object
	 * @param v Version
	 * @param action String name of an ActionEnum
	 */
	public static void applyActionOnVersion (Version v, String action) {
		ActionEnum ae = ActionEnum.getActionEnum(action);
		if (null == ae) {
			System.out.println("WARN: action is null, version will not be mutated");
		} else {
			applyActionOnVersion(v, ae);
		}
	}
	
	/**
	 * This method mutates version by setting its maven snapshot status to specified parameter
	 * @param v
	 * @param status
	 */
	public static void setMavenSnapshotStatus (Version v, boolean status) {
		v.setSnapshot(status);
	}
	
	/**
	 * This method parses date in the string and sets version to that date mutating it
	 * @param v Version
	 * @param dateStr String representation of date
	 */
	public static void setVersionDateFromString (Version v, String dateStr) {
		if (StringUtils.isNotEmpty(dateStr)) {
			LocalDate ld = LocalDate.parse(dateStr);
			ZonedDateTime zdate = ZonedDateTime.of(ld, LocalTime.parse("05:00"), (ZoneId.of("UTC")));
			v.setDate(zdate);
		} else {
			System.out.println("WARN: date string is empty, version will not be mutated");
		}
	}
	
	/**
	 * This method returns base version based on supplied schema, modifier and metadata
	 * @param schema String
	 * @param modifier String
	 * @param metadata String
	 * @return version String
	 */
	public static String getBaseVerWithModMeta(String schema, String modifier, String metadata) {
		Version v = VersionUtils.initializeVersionWithModMeta(schema, modifier, metadata);
		return v.constructVersionString();
	}
	
	/**
	 * This method returns a CalVer version based on one of preset types
	 * @param vt preset VersionType enum
	 * @param modifier String
	 * @param metadata String
	 * @return version String
	 */
	public static String getCalverType(VersionType vt, String modifier, String metadata) {
		return getBaseVerWithModMeta(vt.getSchema(), modifier, metadata);
	}
	
	/**
	 * This method returns a Ubuntu style CalVer version
	 * @return Ubuntu style version
	 */
	public static String getUbuntuCalver() {
		return getCalverType(VersionType.CALVER_UBUNTU, null, null);
	}
	
	/**
	 * This method returns a Reliza style CalVer vesion
	 * @param modifier String
	 * @param metadata String
	 * @return Reliza style version
	 */
	public static String getRelizaCalver(String modifier, String metadata) {
		if (StringUtils.isEmpty(modifier)) {
			modifier = Constants.BASE_MODIFIER;
		}
		return getCalverType(VersionType.CALVER_RELIZA, modifier, metadata);
	}
	
	/**
	 * This method returns a Reliza style 2020 CalVer vesion
	 * @param modifier String
	 * @param metadata String
	 * @return Reliza style 2020 version
	 */
	public static String getRelizaCalver2020(String modifier, String metadata) {
		if (StringUtils.isEmpty(modifier)) {
			modifier = Constants.BASE_MODIFIER;
		}
		return getCalverType(VersionType.CALVER_RELIZA_2020, modifier, metadata);
	}
}
