package rocks.friedrich.engine_omega.internal.physics;

import rocks.friedrich.engine_omega.internal.annotations.Internal;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Wrapper-Klasse für {@link org.jbox2d.dynamics.FixtureDef} zur Übersetzung zwischen Engine-Features und
 * JBox2D-Features.
 *
 * @author Michael Andonie
 * @see rocks.friedrich.engine_omega.internal.physics.PhysicsData
 */
public class FixtureData {

    private double density = PhysicsData.DEFAULT_DENSITY;
    private boolean isDensitySet = false;
    private double friction = PhysicsData.DEFAULT_FRICTION;
    private boolean isFrictionSet = false;
    private double restitution = PhysicsData.DEFAULT_RESTITUTION;
    private boolean isRestitutionSet = false;
    private Shape shape;

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        isDensitySet = true;
        this.density = density;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        isFrictionSet = true;
        this.friction = friction;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        isRestitutionSet = true;
        this.restitution = restitution;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void setSensor(boolean sensor) {
        isSensor = sensor;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        throw new UnsupportedOperationException("Custom Filter sind noch nicht implementiert. Ist für future release geplant.");
    }

    private boolean isSensor = false;
    private boolean isSensorSet = false;

    /**
     * Erstellt eine neue Fixture-Data.
     *
     * @param shape Die Shape, die diese Fixture hat.
     */
    public FixtureData(Shape shape) {
        this.shape = shape;
    }

    /**
     * Collision-Filter. Ist default by Standard. Kann beliebig gesetzt werden; in einem zukünftigen Release.
     * Siehe: https://www.iforce2d.net/b2dtut/collision-filtering
     * <p>
     * TODO: Implement filter Functionality
     * </p>
     */
    private Filter filter = new Filter();

    /**
     * Generiert eine JBox2D Fixture-Definition, die den aktuellen Settings dieser Fixture-Data entspricht.
     */
    @Internal
    FixtureDef createFixtureDef(PhysicsData parent) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = (float) (isDensitySet ? density : parent.getGlobalDensity());
        fixtureDef.friction = (float) (isFrictionSet ? friction : parent.getGlobalFriction());
        fixtureDef.restitution = (float) (isRestitutionSet ? restitution : parent.getGlobalRestitution());
        fixtureDef.isSensor = isSensorSet ? isSensor : parent.getType().isSensor();
        fixtureDef.filter = filter;
        fixtureDef.shape = shape;
        return fixtureDef;
    }

    /**
     * Generiert eine Fixture Data aus einer JBox2D-Fixture
     *
     * @param fixture Die JBox2D-Fixture, die als Engine Fixture Data generiert werden solll
     *
     * @return Eine Engine-Fixture-Data, die die JBox2D-Fixture-Def vollständig beschreibt.
     */
    @Internal
    public static FixtureData fromFixture(Fixture fixture) {
        FixtureData data = new FixtureData(fixture.m_shape);

        data.setRestitution(fixture.m_restitution);
        data.setDensity(fixture.m_density);
        data.setFriction(fixture.m_density);
        data.setSensor(fixture.m_isSensor);
        data.filter = fixture.m_filter; //TODO: Adapt Filter Line once Filter is implemented

        return data;
    }
}
