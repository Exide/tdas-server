package org.arabellan.tdas.components;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tdas.math.Vector;
import org.arabellan.tdas.model.Component;

import static org.arabellan.tdas.utils.MathConverter.degreesToRadians;

@Slf4j
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class Thruster extends Component {

    @Builder.Default
    boolean isThrustingForward = false;

    @Builder.Default
    boolean isThrustingBackward = false;

    @Builder.Default
    float distancePerSecond = 5;

    @Builder.Default
    float maximumVelocity = 5;

    @Builder.Default
    boolean isRotatingRight = false;

    @Builder.Default
    boolean isRotatingLeft = false;

    @Builder.Default
    float degreesPerSecond = 360;

    public boolean update(double deltaTimeSeconds) {
        boolean positionChanged = updatePosition(deltaTimeSeconds);
        boolean rotationChanged = updateRotation(deltaTimeSeconds);
        return positionChanged || rotationChanged;
    }

    private boolean updatePosition(double deltaTimeSeconds) {
        if (!isThrustingForward && !isThrustingBackward) return false;

        Transform transform = getParent().getComponent(Transform.class);
        RigidBody rigidBody = getParent().getComponent(RigidBody.class);
        double distanceChange = 0;

        if (isThrustingForward) {
            distanceChange += distancePerSecond * deltaTimeSeconds;
        }

        if (isThrustingBackward) {
            distanceChange -= distancePerSecond * deltaTimeSeconds;
        }

        if (distanceChange == 0) return false;

        // determine forward vector
        double rotationRadians = degreesToRadians(transform.getRotation());
        double x = Math.sin(rotationRadians);
        double y = Math.cos(rotationRadians);
        Vector forward = new Vector(x, y);

        // calculate change in position
        Vector translation = forward.multiply(distanceChange);

        // calculate new absolute position
        Vector updatedVelocity = rigidBody.getVelocity().add(translation);

        // enforce maximum velocity
        if (rigidBody.getVelocity().magnitude() > maximumVelocity) {
            updatedVelocity = rigidBody.getVelocity().normalize().multiply(maximumVelocity);
        }

        rigidBody.setVelocity(updatedVelocity);
        return true;
    }

    private boolean updateRotation(double deltaTimeSeconds) {
        if (!isRotatingLeft && !isRotatingRight) return false;

        Transform transform = getParent().getComponent(Transform.class);
        float degreesChange = 0;

        if (isRotatingLeft) {
            degreesChange -= degreesPerSecond * deltaTimeSeconds;
        }

        if (isRotatingRight) {
            degreesChange += degreesPerSecond * deltaTimeSeconds;
        }

        if (degreesChange == 0) return false;

        transform.addToRotation(degreesChange);
        return true;
    }

    public void startThrusting(String direction) {
        if (!direction.equals("forward") && !direction.equals("backward")) {
            throw new IllegalArgumentException("direction must be 'forward' or 'backward'");
        }

        log.debug(String.format("%s started thrusting %s", getParent().getId(), direction));

        if (direction.equals("forward") && !isThrustingForward) {
            isThrustingForward = true;
        }

        if (direction.equals("backward") && !isThrustingBackward) {
            isThrustingBackward = true;
        }
    }

    public void stopThrusting(String direction) {
        if (!direction.equals("forward") && !direction.equals("backward")) {
            throw new IllegalArgumentException("direction must be 'forward' or 'backward'");
        }

        log.debug(String.format("%s stopped thrusting %s", getParent().getId(), direction));

        if (direction.equals("forward") && isThrustingForward) {
            isThrustingForward = false;
        }

        if (direction.equals("backward") && isThrustingBackward) {
            isThrustingBackward = false;
        }
    }

    public void startRotating(String direction) {
        if (!direction.equals("left") && !direction.equals("right")) {
            throw new IllegalArgumentException("direction must be 'left' or 'right'");
        }

        log.debug(String.format("%s started rotating %s", getParent().getId(), direction));

        if (direction.equals("left") && !isRotatingLeft) {
            isRotatingLeft = true;
        }

        if (direction.equals("right") && !isRotatingRight) {
            isRotatingRight = true;
        }
    }

    public void stopRotating(String direction) {
        if (!direction.equals("left") && !direction.equals("right")) {
            throw new IllegalArgumentException("direction must be 'left' or 'right'");
        }

        log.debug(String.format("%s stopped rotating %s", getParent().getId(), direction));

        if (direction.equals("left") && isRotatingLeft) {
            isRotatingLeft = false;
        }

        if (direction.equals("right") && isRotatingRight) {
            isRotatingRight = false;
        }
    }

}
