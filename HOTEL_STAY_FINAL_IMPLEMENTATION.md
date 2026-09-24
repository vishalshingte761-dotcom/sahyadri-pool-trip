# Sahyadri Pool & Trip — Hotel Stay Final Implementation

This build adds the hotel stay workflow without changing the existing trip, agency, fort, destination and authentication flows.

## Included
- Search approved active registered properties by location/base village text.
- Live date-wise room availability using overlapping confirmed bookings.
- Property photo URL, check-in time, check-out time and payment mode.
- Traveler stay booking with easy dates → rooms → payment flow.
- Pay at Hotel and Online selection recorded in booking state.
- Optional Sahyadri coupon field with server-side validation against property coupon settings.
- Integer INR display formatting in the stay UI and updated property/agency displays.
- Hotel dashboard guest Check-in and Check-out actions with booking lifecycle state.
- Responsive mobile stay booking refinements.

## Payment note
Online selection records ONLINE_PENDING until the existing Razorpay checkout/webhook integration is connected for property bookings. Pay at Hotel records PAY_AT_HOTEL.
