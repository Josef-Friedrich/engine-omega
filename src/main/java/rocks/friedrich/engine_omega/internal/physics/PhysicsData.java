package rocks.friedrich.engine_omega.internal.physics;

import rocks.friedrich.engine_omega.actor.Actor;
import rocks.friedrich.engine_omega.actor.BodyType;
import rocks.friedrich.engine_omega.internal.annotations.Internal;
import rocks.friedrich.engine_omega.Vector;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Diese Klasse wrappt die wesentlichen physikalischen Eigenschaften eines <code>Actor</code>-Objekts.
 * Die
 */
@Internal
public class PhysicsData {

    static final double DEFAULT_DENSITY = 10f;
    static final double DEFAULT_FRICTION = 0f;
    static final double DEFAULT_RESTITUTION = 0.5f;
    static final BodyType DEFAULT_BODY_TYPE = BodyType.SENSOR;

    private boolean rotationLocked = false;

    private double x = 0;
    private double y = 0;

    private double rotation = 0;

    private double globalDensity = DEFAULT_DENSITY;
    private double globalFriction = DEFAULT_FRICTION;
    private double globalRestitution = DEFAULT_RESTITUTION;
    private double torque = 0;
    private double angularVelocity = 0;
    private double gravityScale = 1;
    private double linearDamping = 0;
    private double angularDamping = 0;

    private Double mass;

    private Vector velocity = Vector.NULL;

    private BodyType type = DEFAULT_BODY_TYPE;

    private Supplier<List<FixtureData>> fixtures;

    /**
     * Erstellt ein Proxydatenset basierend auf einem JBox2D-Body
     *
     * @param body Der zu kopierende Körper.
     */
    public static PhysicsData fromBody(Body body, BodyType type) {
        PhysicsData data = new PhysicsData(extractFixturesFromBody(body));

        //Global Fixture Vals are blindly taken from first Fixture
        data.setGlobalDensity(body.m_fixtureList.m_density);
        data.setGlobalFriction(body.m_fixtureList.m_friction);
        data.setGlobalRestitution(body.m_fixtureList.m_restitution);

        data.setRotationLocked(body.isFixedRotation());
        data.setGravityScale(body.m_gravityScale);
        data.setX(body.getPosition().x);
        data.setY(body.getPosition().y);
        data.setRotation((double) Math.toDegrees(body.getAngle()));
        data.setTorque(body.m_torque);
        data.setVelocity(Vector.of(body.m_linearVelocity));
        data.setAngularVelocity((double) Math.toDegrees(body.m_angularVelocity) / 360);
        data.setType(type);

        data.setAngularDamping(body.getAngularDamping());
        data.setLinearDamping(body.getLinearDamping());

        return data;
    }

    public static Supplier<List<FixtureData>> extractFixturesFromBody(Body body) {
        final ArrayList<FixtureData> fixtureData = new ArrayList<>();

        for (Fixture fixture = body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            fixtureData.add(FixtureData.fromFixture(fixture));
        }

        return () -> fixtureData;
    }

    /**
     * Default-Konstruktor erstellt ein Proxydatenset mit Standardwerten.
     *
     * @param fixtures Eine Funktion, die eine gut abschätzende Shape für das zugehörige Actor-Objekt berechnet.
     */
    public PhysicsData(Supplier<List<FixtureData>> fixtures) {
        setFixtures(fixtures);
    }

    /**
     * Erstellt Fixture-Definitions für alle Shapes des Actors.
     */
    public FixtureDef[] createFixtureDefs() {
        List<FixtureDef> fixtureDefs = new ArrayList<>();
        List<FixtureData> fixtureList = this.getFixtures().get();

        for (FixtureData fixtureData : fixtureList) {
            fixtureDefs.add(fixtureData.createFixtureDef(this));
        }

        return fixtureDefs.toArray(new FixtureDef[0]);
    }

    /**
     * Erstellt eine Body-Definition für den Actor
     */
    public BodyDef createBodyDef() {
        BodyDef bodyDef = new BodyDef();

        bodyDef.angle = (float) Math.toRadians(this.getRotation());
        bodyDef.position.set(new Vec2((float) getX(), (float) getY()));
        bodyDef.fixedRotation = isRotationLocked();
        bodyDef.linearVelocity = getVelocity().toVec2();
        bodyDef.angularVelocity = (float) Math.toRadians(getAngularVelocity() * 360);
        bodyDef.type = getType().toBox2D();
        bodyDef.active = true;
        bodyDef.gravityScale = (float) gravityScale;
        bodyDef.angularDamping =  (float) angularDamping;
        bodyDef.linearDamping =  (float) linearDamping;

        return bodyDef;
    }

    /**
     * Erstellt einen vollständigen Body basierend auf allen ProxyDaten.
     * <b>Macht keine Prüfungen, ob ein entsprechender Body bereits gebaut wurde.</b>
     *
     * @param world Der World-Handler, in dessen World der Body erstellt werden soll.
     *
     * @return Der frisch erstellte Body nach allen Spezifikationen der Proxy-Daten.
     */
    Body createBody(WorldHandler world, Actor actor) {
        Body body = world.createBody(createBodyDef(), actor);

        for (FixtureDef fixtureDef : createFixtureDefs()) {
            body.createFixture(fixtureDef);
        }

        return body;
    }

    @Internal
    public FixtureData[] generateFixtureData() {
        List<FixtureData> data = fixtures.get();
        return data.toArray(new FixtureData[data.size()]);
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public Double getMass() {
        return mass;
    }

    public boolean isRotationLocked() {
        return rotationLocked;
    }

    public void setRotationLocked(boolean rotationLocked) {
        this.rotationLocked = rotationLocked;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(double linearDamping) {
        this.linearDamping = linearDamping;
    }

    public double getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(double angularDamping) {
        this.angularDamping = angularDamping;
    }

    public double getGlobalDensity() {
        return globalDensity;
    }

    public void setGlobalDensity(double globalDensity) {
        this.globalDensity = globalDensity;
    }

    public double getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(double factor) {
        this.gravityScale = factor;
    }

    public double getGlobalFriction() {
        return globalFriction;
    }

    public void setGlobalFriction(double globalFriction) {
        this.globalFriction = globalFriction;
    }

    public double getGlobalRestitution() {
        return globalRestitution;
    }

    public void setGlobalRestitution(double globalRestitution) {
        this.globalRestitution = globalRestitution;
    }

    public double getTorque() {
        return torque;
    }

    public void setTorque(double torque) {
        this.torque = torque;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public BodyType getType() {
        return type;
    }

    public void setType(BodyType type) {
        this.type = type;
    }

    public Supplier<List<FixtureData>> getFixtures() {
        return fixtures;
    }

    public void setFixtures(Supplier<List<FixtureData>> fixtures) {
        this.fixtures = fixtures;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
}
