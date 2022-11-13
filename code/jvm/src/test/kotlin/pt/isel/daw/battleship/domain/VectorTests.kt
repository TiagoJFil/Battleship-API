package pt.isel.daw.battleship.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VectorTests {

    @Test
    fun `creating a vector where the orientation is not horizontal neither vertical throws IllegalArgumentException`() {
        val initialSquare = Square(1, 1)
        val finalSquare = Square(2, 2)

        assertThrows<IllegalArgumentException> {
            Vector(initialSquare, finalSquare)
        }
    }

    @Test
    fun `creating a vector where the orientation is horizontal`() {
        val initialSquare = Square(1, 1)
        val finalSquare = Square(1, 2)

        val vector = Vector(initialSquare, finalSquare)

        assertEquals(Orientation.Horizontal, vector.orientation)
        assertEquals(1, vector.direction)
        assertEquals(1, vector.absDirection)
        assertEquals(1, vector.factor)
    }

    @Test
    fun `creating a vector where the orientation is vertical`() {
        val initialSquare = Square(1, 1)
        val finalSquare = Square(2, 1)

        val vector = Vector(initialSquare, finalSquare)

        assertEquals(Orientation.Vertical, vector.orientation)
        assertEquals(1, vector.direction)
        assertEquals(1, vector.absDirection)
        assertEquals(1, vector.factor)
    }

    @Test
    fun `creating a vector where the orientation is horizontal and the direction is negative`() {
        val initialSquare = Square(1, 2)
        val finalSquare = Square(1, 1)

        val vector = Vector(initialSquare, finalSquare)

        assertEquals(Orientation.Horizontal, vector.orientation)
        assertEquals(-1, vector.direction)
        assertEquals(1, vector.absDirection)
        assertEquals(-1, vector.factor)
    }

    @Test
    fun `creating a vector where the orientation is vertical and the direction is negative`() {
        val initialSquare = Square(2, 1)
        val finalSquare = Square(1, 1)

        val vector = Vector(initialSquare, finalSquare)

        assertEquals(Orientation.Vertical, vector.orientation)
        assertEquals(-1, vector.direction)
        assertEquals(1, vector.absDirection)
        assertEquals(-1, vector.factor)
    }



}