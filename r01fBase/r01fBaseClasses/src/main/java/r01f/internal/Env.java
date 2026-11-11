package r01f.internal;

import java.util.Collection;
import java.util.Iterator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.oids.CommonOIDs.Environment;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum Env
 implements EnumExtended<Env> {
	LOC(Lists.newArrayList(Environment.forId("loc"))),
	CAN(Lists.newArrayList(Environment.forId("can"))),
	DEV(Lists.newArrayList(Environment.forId("dev"))),
	INT(Lists.newArrayList(Environment.forId("int"))),
	SAND(Lists.newArrayList(Environment.forId("sand"),Environment.forId("test"))),
	PROD(Lists.newArrayList(Environment.forId("prod")));

	@Getter private final Collection<Environment> _envs;

	public Environment getDefaultEnvironment() {
		return _envs != null ? _envs.stream()
									.findFirst()
									.orElse(null)
							 : null;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static boolean canBeFrom(final Environment env) {
		boolean outCanBe = true;
		try {
			Env.from(env);
		} catch (IllegalArgumentException illArgEx) {
			outCanBe = false;
		}
		return outCanBe;
	}
	public static Env guessFromSystemProps() {
		Environment env = Environment.guessFromSystemProps();
		return Env.from(env);
	}
	public static Env from(final Environment env) {
		if (env == null) throw new IllegalArgumentException("Env cannot be created from a NULL environment!");
		Env outEnv = null;
		for (Env e : Env.values()) {
			for (Environment environment : e.getEnvs()) {
				// maybe the environment is like (loc|can|dev|int|test|prod)_<initiative>
				if (environment.is(env)
				 || env.getId().startsWith(environment.getId())) {
					outEnv = e;
					break;
				}
			}
			if (outEnv != null) break;
		}
		if (outEnv == null && Environment.DEFAULT.is(env)) outEnv = Env.LOC;	// default == local

		if (outEnv == null) throw new IllegalArgumentException(Strings.customized("{} is NOT a valid {} value (valid ones are {})",
																			 	  env,Env.class,Env.availableValuesDebugInfo()));
		return outEnv;
	}
	public static String availableValuesDebugInfo() {
		StringBuilder sb = new StringBuilder(Env.values().length * 5);
		for (Iterator<Env> envIt = Lists.newArrayList(Env.values()).iterator(); envIt.hasNext(); ) {
			for (Iterator<Environment> environmentIt = envIt.next().getEnvs().iterator(); environmentIt.hasNext(); ) {
				sb.append(environmentIt.next());
				if (environmentIt.hasNext()) sb.append(", ");
			}
			if (envIt.hasNext()) sb.append(", ");
		}
		return sb.toString();
	}
}
