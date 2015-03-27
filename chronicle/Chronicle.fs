module Chronicle

open System

type User(name: string, trackCount: int) =
    member m.Name = name
    member m.TrackCount = trackCount

type Artist(name: string,
            mbid: Guid option) =
    member m.Name = name
    member m.Mbid = mbid

type Track(name: string,
           artist: Artist,
           mbid: Guid option) =
    member m.Name = name
    member m.Artist = artist
    member m.Mbid = mbid
