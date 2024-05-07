/*
 * Engine Omega ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2020 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.friedrich.engine_omega.actor;

import rocks.friedrich.engine_omega.internal.annotations.API;

public final class RevoluteJoint extends Joint<org.jbox2d.dynamics.joints.RevoluteJoint> {
    private double lowerLimit;
    private double upperLimit;

    private boolean motorEnabled;
    private boolean limitEnabled;

    /**
     * Geschwindigkeit in Umdrehungen / Sekunde
     */
    private double motorSpeed;
    private double maximumMotorTorque;

    @API
    public void setMaximumMotorTorque(double maximumMotorTorque) {
        this.maximumMotorTorque = maximumMotorTorque;
        this.motorEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setMaxMotorTorque((float) maximumMotorTorque);
            joint.enableMotor(true);
        }
    }

    @API
    public double getMaximumMotorTorque() {
        return maximumMotorTorque;
    }

    @API
    public double getLowerLimit() {
        return lowerLimit;
    }

    @API
    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits((float) lowerLimit, (float)upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public double getUpperLimit() {
        return upperLimit;
    }

    @API
    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits((float) lowerLimit, (float)  upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public void setLimits(double lowerLimit, double upperLimit) {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.limitEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.setLimits((float) lowerLimit, (float) upperLimit);
            joint.enableLimit(true);
        }
    }

    @API
    public double getMotorSpeed() {
        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            return (double) Math.toDegrees(joint.getMotorSpeed()) / 360;
        }

        return motorSpeed;
    }

    @API
    public void setMotorSpeed(double motorSpeed) {
        this.motorSpeed = motorSpeed;
        this.motorEnabled = true;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            double speed = (double) Math.toRadians(motorSpeed * 360);
            joint.setMotorSpeed((float) speed);
            joint.enableMotor(true);
        }
    }

    @API
    public boolean isMotorEnabled() {
        return motorEnabled;
    }

    @API
    public void setMotorEnabled(boolean motorEnabled) {
        this.motorEnabled = motorEnabled;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.enableMotor(motorEnabled);
        }
    }

    @API
    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    @API
    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;

        org.jbox2d.dynamics.joints.RevoluteJoint joint = getJoint();
        if (joint != null) {
            joint.enableMotor(limitEnabled);
        }
    }

    @Override
    protected void updateCustomProperties(org.jbox2d.dynamics.joints.RevoluteJoint joint) {
        joint.setMotorSpeed((float) Math.toRadians(motorSpeed * 360));
        joint.setMaxMotorTorque((float) maximumMotorTorque);
        joint.setLimits((float) lowerLimit,(float)  upperLimit);
        joint.enableLimit(limitEnabled);
        joint.enableMotor(motorEnabled);
    }
}
