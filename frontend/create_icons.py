#!/usr/bin/env python3
"""
Create professional transparent PNG icons for the TrainLink application.
Uses PIL (Pillow) to draw clean, modern icons.
"""

from PIL import Image, ImageDraw
import os

def create_dashboard_icon(size=24):
    """Create a dashboard/grid icon"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Draw 4 rounded rectangles in a grid
    margin = 2
    rect_size = (size - 3 * margin) // 2
    
    # Top-left
    draw.rounded_rectangle([margin, margin, margin + rect_size, margin + rect_size], 
                          radius=2, fill='#f97316')
    # Top-right
    draw.rounded_rectangle([margin * 2 + rect_size, margin, size - margin, margin + rect_size], 
                          radius=2, fill='#f97316')
    # Bottom-left
    draw.rounded_rectangle([margin, margin * 2 + rect_size, margin + rect_size, size - margin], 
                          radius=2, fill='#f97316')
    # Bottom-right
    draw.rounded_rectangle([margin * 2 + rect_size, margin * 2 + rect_size, size - margin, size - margin], 
                          radius=2, fill='#f97316')
    
    return img

def create_users_icon(size=24):
    """Create a users/people icon"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Draw two overlapping circles (heads) and bodies
    # First person
    draw.ellipse([3, 3, 10, 10], fill='#f97316')  # Head
    draw.rounded_rectangle([2, 10, 11, 18], radius=3, fill='#f97316')  # Body
    
    # Second person (slightly offset)
    draw.ellipse([13, 3, 20, 10], fill='#f97316')  # Head
    draw.rounded_rectangle([12, 10, 21, 18], radius=3, fill='#f97316')  # Body
    
    return img

def create_trains_icon(size=24):
    """Create a train icon"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Train body
    draw.rounded_rectangle([2, 8, 22, 16], radius=2, fill='#f97316')
    
    # Train front
    draw.rounded_rectangle([18, 6, 22, 18], radius=2, fill='#f97316')
    
    # Windows
    draw.rectangle([4, 10, 7, 13], fill='white')
    draw.rectangle([9, 10, 12, 13], fill='white')
    draw.rectangle([14, 10, 17, 13], fill='white')
    
    # Wheels
    draw.ellipse([4, 15, 8, 19], fill='#374151')
    draw.ellipse([12, 15, 16, 19], fill='#374151')
    
    return img

def create_schedules_icon(size=24):
    """Create a calendar/schedule icon"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Calendar base
    draw.rounded_rectangle([3, 4, 21, 20], radius=2, fill='#f97316')
    
    # Calendar header
    draw.rectangle([3, 4, 21, 9], fill='#ea580c')
    
    # Calendar rings
    draw.rectangle([7, 2, 9, 6], fill='#374151')
    draw.rectangle([15, 2, 17, 6], fill='#374151')
    
    # Calendar grid dots
    for row in range(2):
        for col in range(4):
            x = 6 + col * 3
            y = 11 + row * 3
            draw.ellipse([x, y, x + 1, y + 1], fill='white')
    
    return img

def create_bookings_icon(size=24):
    """Create a ticket/booking icon"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Ticket base
    draw.rounded_rectangle([3, 6, 21, 18], radius=2, fill='#f97316')
    
    # Ticket perforation (dotted line)
    for i in range(4):
        y = 8 + i * 2
        draw.ellipse([11, y, 13, y + 1], fill='white')
    
    # Ticket details (lines)
    draw.rectangle([5, 8, 10, 9], fill='white')
    draw.rectangle([5, 11, 9, 12], fill='white')
    draw.rectangle([14, 8, 19, 9], fill='white')
    draw.rectangle([14, 11, 18, 12], fill='white')
    
    return img

def create_logout_icon(size=24):
    """Create a logout/exit icon"""
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Door frame
    draw.rounded_rectangle([3, 3, 15, 21], radius=2, outline='#ef4444', width=2)
    
    # Arrow pointing right (exit)
    # Arrow shaft
    draw.rectangle([12, 11, 20, 13], fill='#ef4444')
    
    # Arrow head
    points = [(20, 12), (22, 9), (22, 15)]
    draw.polygon(points, fill='#ef4444')
    
    return img

def main():
    """Generate all icons"""
    icons_dir = "/home/mohssine/Desktop/projects_gi2/java_project/tainLink/frontend/src/main/resources/icons"
    
    # Create icons with high quality
    icons = {
        'dashboard_transparent.png': create_dashboard_icon,
        'users_transparent.png': create_users_icon,
        'trains_transparent.png': create_trains_icon,
        'schedules_transparent.png': create_schedules_icon,
        'bookings_transparent.png': create_bookings_icon,
        'logout_transparent.png': create_logout_icon,
    }
    
    for filename, icon_func in icons.items():
        # Create at higher resolution for better quality
        icon = icon_func(48)  # Create at 48x48 for high quality
        # Resize to 24x24 with high quality resampling
        icon = icon.resize((24, 24), Image.Resampling.LANCZOS)
        
        filepath = os.path.join(icons_dir, filename)
        icon.save(filepath, 'PNG', optimize=True)
        print(f"Created: {filepath}")
    
    print("All icons created successfully!")

if __name__ == "__main__":
    main()